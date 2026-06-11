package com.travelcompanion.location

import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.travelcompanion.utils.AppConstants
import com.travelcompanion.utils.PermissionUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PlatformLocationProvider @Inject constructor(@ApplicationContext private val context: Context) : LocationProvider {

    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private var listener: LocationListener? = null

    override fun hasLocationPermission(context: Context): Boolean = PermissionUtils.hasLocationPermissions(context)

    override fun getCurrentLocation(onSuccess: (Location) -> Unit, onFailure: (Exception) -> Unit) {
        if (!hasLocationPermission(context)) {
            onFailure(Exception("Location permission not granted"))
            return
        }

        // Controllo esplicito richiesto da Lint: checkSelfPermission deve precedere ogni chiamata a LocationManager
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (!fine && !coarse) {
            onFailure(Exception("Location permission not granted"))
            return
        }

        val provider = when {
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
            else -> null
        }

        provider?.let {
            val last = locationManager.getLastKnownLocation(it)
            if (last != null) {
                onSuccess(last)
                return
            }

            listener = LocationListener { loc ->
                onSuccess(loc)
                locationManager.removeUpdates(listener!!)
            }

            locationManager.requestLocationUpdates(
                it,
                AppConstants.Tracking.LOCATION_UPDATE_INTERVAL_MS,
                AppConstants.Tracking.LOCATION_MIN_DISTANCE_METERS,
                listener!!,
                Looper.getMainLooper()
            )
        } ?: run { onFailure(Exception("No location provider available")) }
    }

    override fun startLocationUpdates(onLocation: (Location) -> Unit, onAvailabilityChanged: ((Boolean) -> Unit)?) {
        if (!hasLocationPermission(context)) return

        // Stesso controllo Lint per startLocationUpdates
        val fine2 = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarse2 = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (!fine2 && !coarse2) return

        // Si registra lo stesso listener su entrambi i provider se disponibili,
        // così si ottiene la posizione dal più veloce tra GPS e rete
        listener = LocationListener { loc -> onLocation(loc) }

        try {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    AppConstants.Tracking.LOCATION_UPDATE_INTERVAL_MS,
                    AppConstants.Tracking.LOCATION_MIN_DISTANCE_METERS,
                    listener!!,
                    Looper.getMainLooper()
                )
            }
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    AppConstants.Tracking.LOCATION_UPDATE_INTERVAL_MS,
                    AppConstants.Tracking.LOCATION_MIN_DISTANCE_METERS,
                    listener!!,
                    Looper.getMainLooper()
                )
            }
        } catch (e: SecurityException) {
            // Non dovrebbe accadere grazie ai controlli precedenti, ma lo catturiamo per sicurezza
        }
    }

    override fun stopLocationUpdates() {
        listener?.let { locationManager.removeUpdates(it) }
        listener = null
    }
}
