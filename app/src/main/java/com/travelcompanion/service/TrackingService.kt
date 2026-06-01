package com.travelcompanion.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.location.Location
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.travelcompanion.MainActivity
import com.travelcompanion.R
import com.travelcompanion.utils.AppConstants
import com.travelcompanion.utils.DateUtils
import com.travelcompanion.data.db.AppDatabase
import com.travelcompanion.data.db.entities.JourneyEntity
import com.travelcompanion.domain.model.Coordinate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import javax.inject.Inject

/**
 * Foreground service for GPS tracking.
 * Consolidates all tracking logic in a single service.
 *
 * Features:
 * - Runtime permission checks
 * - Periodic database persistence
 * - StateFlow for reactive updates (no BroadcastReceiver)
 * - Memory-efficient coordinate buffering
 * - Real-time notification updates
 */
@AndroidEntryPoint
class TrackingService : Service() {

    @Inject
    lateinit var database: AppDatabase

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var notificationManager: NotificationManager

    // Background thread for location updates
    private var locationHandlerThread: HandlerThread? = null
    private var locationHandler: Handler? = null

    // State management
    private var currentTripId: Long = 0
    private var startTime: Long = 0
    private var totalDistance: Float = 0f
    private var lastLocation: Location? = null

    // Coordinate buffer (flush every BUFFER_SIZE points or FLUSH_INTERVAL_MS).
    // Guarded by bufferMutex because handleLocationUpdate appends from one IO coroutine
    // while flushCoordinatesToDatabase may iterate/clear from another.
    private val coordinateBuffer = mutableListOf<Coordinate>()
    private val bufferMutex = Mutex()

    // StateFlow for reactive updates (replaces BroadcastReceiver)
    private val _trackingState = MutableStateFlow<TrackingState>(TrackingState.Idle)
    val trackingState: StateFlow<TrackingState> = _trackingState.asStateFlow()

    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> = _currentLocation.asStateFlow()

    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): TrackingService = this@TrackingService
        fun getTrackingState(): StateFlow<TrackingState> = trackingState
        fun getCurrentLocation(): StateFlow<Location?> = currentLocation
    }

    override fun onBind(intent: Intent): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        notificationManager = getSystemService(NotificationManager::class.java)
        createNotificationChannel()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { handleLocationUpdate(it) }
            }
        }
    }

    @Suppress("MissingPermission")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val tripId = intent?.getLongExtra(EXTRA_TRIP_ID, -1L) ?: -1L
        // Allow restart from any non-active state so a new trip after Stopped/Error works.
        val canStart = _trackingState.value !is TrackingState.Active
        if (tripId > 0 && hasLocationPermission() && canStart) {
            startTracking(tripId)
        }
        return START_NOT_STICKY
    }

    /**
     * Starts GPS tracking for the given trip.
     * Requires ACCESS_FINE_LOCATION permission.
     */
    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    fun startTracking(tripId: Long) {
        // Runtime permission check
        if (!hasLocationPermission()) {
            Timber.e("Location permission not granted, cannot start tracking")
            _trackingState.value = TrackingState.Error("Location permission denied")
            stopSelf()
            return
        }

        currentTripId = tripId
        startTime = System.currentTimeMillis()
        totalDistance = 0f
        lastLocation = null
        serviceScope.launch { bufferMutex.withLock { coordinateBuffer.clear() } }

        startForegroundWithNotification()
        requestLocationUpdates()

        _trackingState.value = TrackingState.Active(
            tripId = tripId,
            duration = 0L,
            distance = 0f,
            pointCount = 0
        )

        Timber.d("Tracking started for trip $tripId")
    }

    /**
     * Stops GPS tracking and persists remaining data.
     */
    fun stopTracking() {
        try {
            fusedLocationClient.removeLocationUpdates(locationCallback)

            // Snapshot before launching: currentTripId/startTime/totalDistance are mutated
            // in handleLocationUpdate and could change before the IO coroutine reads them.
            val finalTripId = currentTripId
            val finalDistanceKm = totalDistance / 1000f
            val finalDurationMs = (System.currentTimeMillis() - startTime).coerceAtLeast(0L)

            _trackingState.value = TrackingState.Stopped

            // Use Application-tied scope so stopSelf() doesn't cancel the writeback.
            // serviceScope is cancelled in onDestroy(); flushing inside it loses data.
            ApplicationScope.scope.launch {
                try {
                    flushCoordinatesToDatabase()
                } catch (e: Exception) {
                    Timber.e(e, "Error flushing coordinates on stop")
                }
                if (finalTripId > 0) {
                    try {
                        val existing = database.tripDao().getTripByIdFlow(finalTripId).first()
                        existing?.let {
                            database.tripDao().updateTrip(
                                it.copy(
                                    totalDistance = finalDistanceKm,
                                    totalDuration = finalDurationMs
                                )
                            )
                        }
                    } catch (e: Exception) {
                        Timber.w(e, "Failed to write back tracking totals to Trip $finalTripId")
                    }
                }
            }

            stopForegroundService()
            stopSelf()

            Timber.d("Tracking stopped for trip $finalTripId")
        } catch (e: Exception) {
            Timber.e(e, "Error stopping tracking")
        }
    }

    // Process-lifetime scope so DB writes survive stopSelf().
    private object ApplicationScope {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }

    private fun startForegroundWithNotification() {
        val notification = buildNotification(
            duration = 0L,
            distance = 0f,
            pointCount = 0
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun stopForegroundService() {
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    private fun requestLocationUpdates() {
        // Reuse the handler thread on restart to avoid leaking a thread per startTracking call
        if (locationHandlerThread == null) {
            locationHandlerThread = HandlerThread("LocationUpdateThread").apply { start() }
            locationHandler = Handler(locationHandlerThread!!.looper)
        }

        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, UPDATE_INTERVAL_MS)
            .setMinUpdateDistanceMeters(MIN_DISTANCE_METERS)
            .setMaxUpdateDelayMillis(UPDATE_INTERVAL_MS * 2)
            .setWaitForAccurateLocation(false)
            .build()

        try {
            fusedLocationClient.requestLocationUpdates(
                request,
                locationCallback,
                locationHandler!!.looper  // Use background thread instead of main looper
            )
        } catch (e: SecurityException) {
            Timber.e(e, "Security exception requesting location updates")
            _trackingState.value = TrackingState.Error("Location permission denied")
            stopSelf()
        }
    }

    private fun handleLocationUpdate(location: Location) {
        _currentLocation.value = location
        // No broadcast: observers should collect currentLocation StateFlow via the binder.

        serviceScope.launch {
            try {
                lastLocation?.let { prev ->
                    val distance = prev.distanceTo(location)
                    if (distance > MIN_DISTANCE_METERS) {
                        totalDistance += distance
                    }
                }
                lastLocation = location

                val coordinate = Coordinate(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    timestamp = java.util.Date(location.time)
                )

                val (pointCount, shouldFlush) = bufferMutex.withLock {
                    coordinateBuffer.add(coordinate)
                    coordinateBuffer.size to shouldFlushBufferLocked()
                }

                val duration = System.currentTimeMillis() - startTime
                _trackingState.value = TrackingState.Active(
                    tripId = currentTripId,
                    duration = duration,
                    distance = totalDistance,
                    pointCount = pointCount
                )

                updateNotification(duration, totalDistance, pointCount)

                if (shouldFlush) {
                    flushCoordinatesToDatabase()
                }
            } catch (e: Exception) {
                Timber.e(e, "Error handling location update")
            }
        }
    }

    // Caller must hold bufferMutex.
    private fun shouldFlushBufferLocked(): Boolean {
        return coordinateBuffer.size >= BUFFER_SIZE ||
                (coordinateBuffer.isNotEmpty() &&
                        System.currentTimeMillis() - coordinateBuffer.first().timestamp.time >= FLUSH_INTERVAL_MS)
    }

    private suspend fun flushCoordinatesToDatabase() {
        val snapshot = bufferMutex.withLock {
            if (coordinateBuffer.isEmpty()) return
            val copy = coordinateBuffer.toList()
            coordinateBuffer.clear()
            copy
        }

        try {
            val journey = JourneyEntity(
                tripId = currentTripId,
                startTime = snapshot.first().timestamp.time,
                endTime = snapshot.last().timestamp.time,
                distance = totalDistance,
                coordinatesJson = com.travelcompanion.data.db.converters.Converters()
                    .coordinatesToJson(snapshot)
            )

            database.journeyDao().insertJourney(journey)
            Timber.d("Flushed ${snapshot.size} coordinates to database")
        } catch (e: Exception) {
            // Re-queue the snapshot so we don't lose points on transient failure
            bufferMutex.withLock { coordinateBuffer.addAll(0, snapshot) }
            Timber.e(e, "Error flushing coordinates to database; re-queued ${snapshot.size} points")
        }
    }

    private fun buildNotification(duration: Long, distance: Float, pointCount: Int): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.tracking_active))
            .setContentText(
                getString(
                    R.string.tracking_stats,
                    DateUtils.formatDistance(distance),
                    DateUtils.formatDuration(duration),
                    pointCount
                )
            )
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    private fun updateNotification(duration: Long, distance: Float, pointCount: Int) {
        // Check POST_NOTIFICATIONS permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Timber.w("POST_NOTIFICATIONS permission not granted, cannot update notification")
                return
            }
        }

        val notification = buildNotification(duration, distance, pointCount)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.tracking_channel_name),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = getString(R.string.tracking_channel_description)
            setShowBadge(false)
        }
        notificationManager.createNotificationChannel(channel)
    }

    private fun hasLocationPermission(): Boolean {
        val fineLocation = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        // On Android 10+ (Q), background location is required for foreground service
        val backgroundLocation = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

        if (fineLocation && !backgroundLocation) {
            Timber.w("ACCESS_BACKGROUND_LOCATION not granted on Android 10+")
        }

        return fineLocation
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()

        // Clean up location handler thread
        locationHandlerThread?.quitSafely()
        locationHandlerThread = null
        locationHandler = null

        Timber.d("TrackingService destroyed")
    }

    /**
     * Sealed class representing tracking states.
     */
    sealed class TrackingState {
        object Idle : TrackingState()
        data class Active(
            val tripId: Long,
            val duration: Long,
            val distance: Float,
            val pointCount: Int
        ) : TrackingState()
        object Stopped : TrackingState()
        data class Error(val message: String) : TrackingState()
    }

    companion object {
        private const val CHANNEL_ID = AppConstants.NotificationChannels.TRACKING
        private const val NOTIFICATION_ID = AppConstants.NotificationIds.TRACKING

        // Location tracking parameters
        private const val UPDATE_INTERVAL_MS = 10_000L // 10 seconds
        private const val MIN_DISTANCE_METERS = 10f

        // Buffer parameters
        private const val BUFFER_SIZE = 50 // Flush every 50 points
        private const val FLUSH_INTERVAL_MS = 60_000L // Or every 60 seconds

        // Intent extras
        const val EXTRA_TRIP_ID = "extra_trip_id"
    }
}
