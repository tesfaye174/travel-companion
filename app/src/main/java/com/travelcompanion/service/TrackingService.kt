package com.travelcompanion.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import com.travelcompanion.R
import com.travelcompanion.data.db.AppDatabase
import com.travelcompanion.domain.model.Coordinate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class TrackingService : Service() {

    @Inject
    lateinit var database: AppDatabase

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var currentTripId: Long = 0
    private val coordinates = mutableListOf<Coordinate>()
    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): TrackingService = this@TrackingService
    }

    override fun onBind(intent: Intent): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createNotificationChannel()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { loc ->
                    handleLocationUpdate(loc)
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun startTracking(tripId: Long) {
        currentTripId = tripId
        startForeground(1, getNotification())

        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10_000L)
            .setMinUpdateDistanceMeters(10f)
            .build()

        fusedLocationClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())
    }

    fun stopTracking() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            stopForeground(true)
        }
        stopSelf()
    }

    private fun handleLocationUpdate(location: Location) {
        serviceScope.launch {
            try {
                // Add coordinate to current journey
                val coordinate = Coordinate(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    timestamp = java.util.Date(System.currentTimeMillis())
                )
                coordinates.add(coordinate)
            } catch (e: Exception) {
                Timber.e(e, "Error handling location update")
            }
        }
    }

    private fun getNotification(): Notification {
        return NotificationCompat.Builder(this, "tracking_channel")
            .setContentTitle("Tracking Attivo")
            .setContentText("Registrazione percorso in corso...")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("tracking_channel", "Tracking", NotificationManager.IMPORTANCE_LOW)
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        const val EXTRA_TRIP_ID = "extra_trip_id"
        const val ACTION_LOCATION_UPDATE = "com.travelcompanion.LOCATION_UPDATE"
    }
}
