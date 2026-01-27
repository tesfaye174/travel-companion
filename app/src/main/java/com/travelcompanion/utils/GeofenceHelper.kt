package com.travelcompanion.utils

import android.content.Context
import android.app.PendingIntent
import com.travelcompanion.location.GeofenceProvider

/**
 * Geofence helper that delegates to a pluggable GeofenceProvider.
 */
class GeofenceHelper(private val context: Context, private val provider: GeofenceProvider) {

    fun getGeofencePendingIntent(): PendingIntent = provider.getGeofencePendingIntent()

    fun addGeofence(id: String, lat: Double, lng: Double, radius: Float) {
        provider.addGeofence(id, lat, lng, radius)
    }

    fun removeGeofence(id: String) {
        provider.removeGeofence(id)
    }
}

