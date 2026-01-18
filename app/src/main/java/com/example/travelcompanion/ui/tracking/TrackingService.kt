package com.example.travelcompanion.ui.tracking

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
import com.example.travelcompanion.R
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class TrackingService : Service() {

    @Inject
    lateinit var notificationManager: NotificationManager

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationCallback: LocationCallback? = null

    // ✅ CORRETTO: Coroutine scope con lifecycle del service
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var currentTripId: Long = -1
    private var coordinates = mutableListOf<com.example.travelcompanion.domain.model.Coordinate>()
    private var startTime: Long = 0

    private val notificationId = 1
    private val channelId = "tracking_channel"

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createNotificationChannel()
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
        val locationRequest = LocationRequest.create().apply {
            interval = 5000
            fastestInterval = 2000
            priority = Priority.PRIORITY_HIGH_ACCURACY
            smallestDisplacement = 10f // 10 metri
        }

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
        val coordinate = com.example.travelcompanion.domain.model.Coordinate(
            latitude = location.latitude,
            longitude = location.longitude,
            timestamp = java.util.Date()
        )
        coordinates.add(coordinate)

        // Salva nel database
        saveCoordinateToDatabase(coordinate)

        // Aggiorna notifica
        updateTrackingNotification(calculateTotalDistance())

        // Invia broadcast per aggiornare UI
        sendLocationUpdate(location)
    }

    private suspend fun saveCoordinateToDatabase(coordinate: com.example.travelcompanion.domain.model.Coordinate) {
        // Implementazione database
        delay(100) // Simulazione
    }

    override fun onDestroy() {
        super.onDestroy()
        // ✅ CRITICO: Cancellare coroutine scope
        serviceScope.cancel()
        stopTracking()
    }

    private fun stopTracking() {
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
        }

        // Salva journey completo
        serviceScope.launch {
            saveCompleteJourney()
        }

        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    companion object {
        const val EXTRA_TRIP_ID = "extra_trip_id"
        const val ACTION_LOCATION_UPDATE = "com.example.travelcompanion.LOCATION_UPDATE"
    }
}