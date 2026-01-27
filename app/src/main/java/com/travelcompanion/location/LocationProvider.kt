package com.travelcompanion.location

import android.content.Context
import android.location.Location

interface LocationProvider {
    fun hasLocationPermission(context: Context): Boolean
    fun getCurrentLocation(onSuccess: (Location) -> Unit, onFailure: (Exception) -> Unit)
    fun startLocationUpdates(onLocation: (Location) -> Unit, onAvailabilityChanged: ((Boolean) -> Unit)? = null)
    fun stopLocationUpdates()
}
