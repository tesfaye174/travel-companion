package com.travelcompanion.ui.tracking

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.travelcompanion.R
import com.google.android.gms.location.*
import com.travelcompanion.domain.repository.ITripRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class TrackingService : Service() {

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var repository: ITripRepository

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationCallback: LocationCallback? = null

    // ✅ CORRETTO: Coroutine scope con lifecycle del service
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var currentTripId: Long = -1
    private var coordinates = mutableListOf<com.travelcompanion.domain.model.Coordinate>()
    private var startTime: Long = 0

    private val notificationId = 1
    private val channelId = "tracking_channel"

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createNotificationChannel()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.getLongExtra(EXTRA_TRIP_ID, -1)?.let { tripId ->
            currentTripId = tripId
            startTime = System.currentTimeMillis()
            startTracking()

            startForeground(notificationId, createTrackingNotification())
        }

        return START_STICKY
    }

    private fun startTracking() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            5000L
        )
            .setMinUpdateIntervalMillis(2000L)
            .setMinUpdateDistanceMeters(10f)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    // ✅ CORRETTO: Usare serviceScope invece di GlobalScope
                    serviceScope.launch {
                        saveLocation(location)
                    }
                }
            }

            override fun onLocationAvailability(availability: LocationAvailability) {
                if (!availability.isLocationAvailable) {
                    Timber.w("Location services unavailable")
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            stopSelf()
            return
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback as LocationCallback,
            Looper.getMainLooper()
        )
    }

    private suspend fun saveLocation(location: Location) = withContext(Dispatchers.IO) {
        val coordinate = com.travelcompanion.domain.model.Coordinate(
            latitude = location.latitude,
            longitude = location.longitude,
            timestamp = java.util.Date()
        )
        coordinates.add(coordinate)

        // Calculate distance
        val totalDistance = calculateTotalDistance()

        // Update notification
        updateTrackingNotification(totalDistance)

        // Broadcast update
        sendLocationUpdate(location, totalDistance)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        stopTracking()
    }

    private fun stopTracking() {
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
        }
        
        // Save journey before stopping service
        // Note: serviceScope is cancelled in onDestroy, but we need to run this.
        // We should use a separate scope or runBlocking if needed, but runBlocking in main thread is bad.
        // Since onDestroy cancels serviceScope, we might miss this.
        // Better: launch in GlobalScope or specific Application scope for saving.
        // For simplicity here, we'll try to use a new scope.
        CoroutineScope(Dispatchers.IO).launch {
            saveCompleteJourney()
        }

        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Tracking", NotificationManager.IMPORTANCE_LOW)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createTrackingNotification(): Notification {
        return getNotificationBuilder("Tracking in progress").build()
    }

    private fun getNotificationBuilder(text: String): NotificationCompat.Builder {
         return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Tracking")
            .setContentText(text)
            .setSmallIcon(com.travelcompanion.R.drawable.ic_location) // Use valid icon
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
    }

    private fun updateTrackingNotification(distance: Float) {
        val text = "Distance: %.2f km".format(distance)
        notificationManager.notify(notificationId, getNotificationBuilder(text).build())
    }

    private fun calculateTotalDistance(): Float {
        if (coordinates.size < 2) return 0f
        var distance = 0f
        for (i in 0 until coordinates.size - 1) {
            val c1 = coordinates[i]
            val c2 = coordinates[i + 1]
            distance += com.travelcompanion.utils.LocationUtils.calculateDistance(
                c1.latitude, c1.longitude, c2.latitude, c2.longitude
            )
        }
        return distance
    }

    private fun sendLocationUpdate(location: Location, distance: Float) {
        val intent = Intent(ACTION_LOCATION_UPDATE)
        intent.putExtra("lat", location.latitude)
        intent.putExtra("lon", location.longitude)
        intent.putExtra("dist", distance)
        sendBroadcast(intent)
    }

    private suspend fun saveCompleteJourney() {
        if (currentTripId == -1L || coordinates.isEmpty()) return
        
        val journey = com.travelcompanion.domain.model.Journey(
            tripId = currentTripId,
            startTime = java.util.Date(startTime),
            endTime = java.util.Date(),
            distance = calculateTotalDistance(),
            coordinates = coordinates.toList()
        )
        try {
            repository.insertJourney(journey)
            recalculateAndPersistTripTotals(currentTripId)
        } catch (e: Exception) {
            Timber.e(e, "Error saving journey")
        }
    }

    private suspend fun recalculateAndPersistTripTotals(tripId: Long) {
        val trip = repository.getTripById(tripId) ?: return
        val journeys = repository.getJourneysByTripId(tripId).first()

        val totalDistanceKm = journeys.sumOf { it.distance.toDouble() }.toFloat()
        val totalDurationMs = journeys.sumOf { j ->
            val end = j.endTime?.time ?: return@sumOf 0L
            val start = j.startTime.time
            (end - start).coerceAtLeast(0L)
        }

        repository.updateTrip(
            trip.copy(
                totalDistance = totalDistanceKm,
                totalDuration = totalDurationMs,
                isTracking = false
            )
        )
    }

    companion object {
        const val EXTRA_TRIP_ID = "extra_trip_id"
        const val ACTION_LOCATION_UPDATE = "com.travelcompanion.LOCATION_UPDATE"
    }
}

