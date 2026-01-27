package com.travelcompanion.location

import android.app.PendingIntent

interface GeofenceProvider {
    fun addGeofence(id: String, lat: Double, lng: Double, radius: Float)
    fun removeGeofence(id: String)
    fun getGeofencePendingIntent(): PendingIntent
}
