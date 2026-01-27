package com.travelcompanion.location

import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.*
import com.google.android.gms.tasks.CancellationTokenSource
import com.travelcompanion.utils.AppConstants
import com.travelcompanion.utils.LocationUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PlayServicesLocationProvider @Inject constructor(@ApplicationContext private val context: Context) : LocationProvider {

    private val fused: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private var callback: LocationCallback? = null

    override fun hasLocationPermission(context: Context): Boolean = LocationUtils.hasLocationPermission(context)

    override fun getCurrentLocation(onSuccess: (Location) -> Unit, onFailure: (Exception) -> Unit) {
        if (!hasLocationPermission(context)) {
            onFailure(Exception("Location permission not granted"))
            return
        }

        val cancellationTokenSource = CancellationTokenSource()
        @Suppress("MissingPermission")
        fused.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.token)
            .addOnSuccessListener { loc -> loc?.let(onSuccess) ?: onFailure(Exception("Location is null")) }
            .addOnFailureListener(onFailure)
    }

    override fun startLocationUpdates(onLocation: (Location) -> Unit, onAvailabilityChanged: ((Boolean) -> Unit)?) {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            AppConstants.Tracking.LOCATION_UPDATE_INTERVAL_MS
        )
            .setMinUpdateIntervalMillis(AppConstants.Tracking.LOCATION_FASTEST_INTERVAL_MS)
            .setMinUpdateDistanceMeters(AppConstants.Tracking.LOCATION_MIN_DISTANCE_METERS)
            .build()

        callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let(onLocation)
            }

            override fun onLocationAvailability(availability: LocationAvailability) {
                onAvailabilityChanged?.invoke(availability.isLocationAvailable)
            }
        }

        if (!hasLocationPermission(context)) return
        @Suppress("MissingPermission")
        fused.requestLocationUpdates(locationRequest, callback as LocationCallback, Looper.getMainLooper())
    }

    override fun stopLocationUpdates() {
        callback?.let {
            fused.removeLocationUpdates(it)
            callback = null
        }
    }
}
