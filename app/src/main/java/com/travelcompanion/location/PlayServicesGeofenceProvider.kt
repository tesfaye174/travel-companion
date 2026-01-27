package com.travelcompanion.location

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.travelcompanion.utils.AppConstants
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PlayServicesGeofenceProvider @Inject constructor(@ApplicationContext private val context: Context) : GeofenceProvider {

    private val geofencingClient = LocationServices.getGeofencingClient(context)

    override fun addGeofence(id: String, lat: Double, lng: Double, radius: Float) {
        val gf = Geofence.Builder()
            .setRequestId(id)
            .setCircularRegion(lat, lng, radius)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
            .build()

        val request = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(gf)
            .build()

        try {
            geofencingClient.addGeofences(request, getGeofencePendingIntent())
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    override fun removeGeofence(id: String) {
        geofencingClient.removeGeofences(listOf(id))
    }

    override fun getGeofencePendingIntent(): PendingIntent {
        val intent = Intent(context, com.travelcompanion.utils.GeofenceBroadcastReceiver::class.java)
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }
}
