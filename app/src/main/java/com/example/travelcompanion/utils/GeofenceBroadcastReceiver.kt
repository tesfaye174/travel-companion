package com.example.travelcompanion.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val event = GeofencingEvent.fromIntent(intent) ?: return
        if (event.hasError()) return

        val transition = event.geofenceTransition
        if (transition == Geofence.GEOFENCE_TRANSITION_ENTER || transition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            val triggeringGeofences = event.triggeringGeofences
            val ids = triggeringGeofences?.joinToString { it.requestId } ?: "Unknown"
            
            showNotification(context, transition, ids)
        }
    }

    private fun showNotification(context: Context, transition: Int, ids: String) {
        val channelId = "geofence_channel"
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Location Alerts", NotificationManager.IMPORTANCE_HIGH)
            manager.createNotificationChannel(channel)
        }

        val action = if (transition == Geofence.GEOFENCE_TRANSITION_ENTER) "Entered" else "Exited"
        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Location Alert")
            .setContentText("$action: $ids")
            .setSmallIcon(android.R.drawable.ic_dialog_map)
            .build()

        manager.notify(3, notification)
    }
}
