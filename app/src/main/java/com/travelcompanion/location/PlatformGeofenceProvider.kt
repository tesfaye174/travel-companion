package com.travelcompanion.location

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper
import timber.log.Timber
import com.travelcompanion.utils.AppConstants
import com.travelcompanion.data.db.AppDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Platform geofence provider using periodic location updates to detect
 * enter/exit transitions. Sends a platform broadcast that `GeofenceBroadcastReceiver`
 * also understands.
 */
class PlatformGeofenceProvider @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: AppDatabase
) : GeofenceProvider {

    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private val geofences = mutableMapOf<String, Triple<Double, Double, Float>>() // id -> (lat, lon, radius)
    private val insideState = mutableMapOf<String, Boolean>()
    private var listener: LocationListener? = null
    private var currentIntervalMs: Long = AppConstants.Tracking.LOCATION_UPDATE_INTERVAL_MS
    private var idleCounter: Int = 0
    private val IDLE_THRESHOLD = 5
    private val LONG_INTERVAL_MS = 60_000L

    init {
        // Load persisted geofences from DB on startup
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val areas = database.geofenceAreaDao().getAll().first()
                areas.forEach { area ->
                    geofences[area.id] = Triple(area.latitude, area.longitude, area.radiusMeters)
                    insideState.putIfAbsent(area.id, false)
                }
                if (geofences.isNotEmpty()) startListeningIfNeeded()
            } catch (e: Exception) {
                Timber.w(e, "Error loading persisted geofences")
            }
        }
    }

    override fun addGeofence(id: String, lat: Double, lng: Double, radius: Float) {
        geofences[id] = Triple(lat, lng, radius)
        insideState.putIfAbsent(id, false)
        startListeningIfNeeded()
        Timber.d("PlatformGeofenceProvider added geofence %s", id)
    }

    override fun removeGeofence(id: String) {
        geofences.remove(id)
        insideState.remove(id)
        if (geofences.isEmpty()) stopListening()
        Timber.d("PlatformGeofenceProvider removed geofence %s", id)
    }

    override fun getGeofencePendingIntent(): PendingIntent {
        val intent = Intent(context, com.travelcompanion.utils.GeofenceBroadcastReceiver::class.java)
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    private fun startListeningIfNeeded() {
        if (listener != null) return

        listener = LocationListener { location ->
            checkGeofences(location)
        }

        try {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    currentIntervalMs,
                    AppConstants.Tracking.LOCATION_MIN_DISTANCE_METERS,
                    listener!!,
                    Looper.getMainLooper()
                )
            } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    currentIntervalMs,
                    AppConstants.Tracking.LOCATION_MIN_DISTANCE_METERS,
                    listener!!,
                    Looper.getMainLooper()
                )
            }
        } catch (e: SecurityException) {
            Timber.w(e, "Permission missing to start location updates for PlatformGeofenceProvider")
        }
    }

    private fun stopListening() {
        listener?.let { locationManager.removeUpdates(it) }
        listener = null
    }

    private fun checkGeofences(location: Location) {
        var anyTransition = false
        geofences.forEach { (id, triple) ->
            val (lat, lon, radius) = triple
            val results = FloatArray(1)
            Location.distanceBetween(location.latitude, location.longitude, lat, lon, results)
            val distanceMeters = results[0]
            val currentlyInside = distanceMeters <= radius
            val wasInside = insideState[id] ?: false
            if (currentlyInside && !wasInside) {
                insideState[id] = true
                sendTransition(id, "ENTER")
                anyTransition = true
            } else if (!currentlyInside && wasInside) {
                insideState[id] = false
                sendTransition(id, "EXIT")
                anyTransition = true
            }
        }

        // Backoff logic: if no transitions for a while, reduce frequency
        if (anyTransition) {
            idleCounter = 0
            if (currentIntervalMs != AppConstants.Tracking.LOCATION_UPDATE_INTERVAL_MS) {
                currentIntervalMs = AppConstants.Tracking.LOCATION_UPDATE_INTERVAL_MS
                restartListeningWithInterval()
            }
        } else {
            idleCounter++
            if (idleCounter >= IDLE_THRESHOLD && currentIntervalMs != LONG_INTERVAL_MS) {
                currentIntervalMs = LONG_INTERVAL_MS
                restartListeningWithInterval()
            }
        }
    }

    private fun restartListeningWithInterval() {
        // Re-register location updates with new interval
        listener?.let {
            try {
                locationManager.removeUpdates(it)
            } catch (e: Exception) {
                // ignore
            }
            listener = null
            startListeningIfNeeded()
        }
    }

    private fun sendTransition(id: String, transition: String) {
        val intent = Intent(AppConstants.PlatformIntents.ACTION_PLATFORM_GEOFENCE)
        intent.putExtra(AppConstants.PlatformIntents.EXTRA_GEOFENCE_ID, id)
        intent.putExtra(AppConstants.PlatformIntents.EXTRA_TRANSITION, transition)
        context.sendBroadcast(intent)
        Timber.d("PlatformGeofenceProvider broadcasted %s for %s", transition, id)
    }
}

