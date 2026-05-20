package com.travelcompanion.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.travelcompanion.R
import com.travelcompanion.data.db.AppDatabase
import com.travelcompanion.data.db.entities.GeofenceEventEntity
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface GeofenceReceiverEntryPoint {
        fun database(): AppDatabase
    }

    override fun onReceive(context: Context, intent: Intent) {
        val database = EntryPointAccessors.fromApplication(
            context.applicationContext,
            GeofenceReceiverEntryPoint::class.java
        ).database()

        // Support both Play Services geofencing intents and platform geofence broadcasts
        if (intent.action == AppConstants.PlatformIntents.ACTION_PLATFORM_GEOFENCE) {
            val id = intent.getStringExtra(AppConstants.PlatformIntents.EXTRA_GEOFENCE_ID) ?: return
            val transitionStr = intent.getStringExtra(AppConstants.PlatformIntents.EXTRA_TRANSITION) ?: return
            val transition = if (transitionStr == "ENTER") Geofence.GEOFENCE_TRANSITION_ENTER else Geofence.GEOFENCE_TRANSITION_EXIT
            persistEvents(database, transition, listOf(id))
            showNotification(context, transition, id)
            return
        }

        val event = GeofencingEvent.fromIntent(intent) ?: return
        if (event.hasError()) return

        val transition = event.geofenceTransition
        if (transition == Geofence.GEOFENCE_TRANSITION_ENTER || transition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            val triggeringGeofences = event.triggeringGeofences
            val ids = triggeringGeofences?.joinToString { it.requestId } ?: "Unknown"

            persistEvents(database, transition, triggeringGeofences?.map { it.requestId }.orEmpty())
            showNotification(context, transition, ids)
        }
    }

    private fun persistEvents(database: AppDatabase, transition: Int, ids: List<String>) {
        val transitionLabel = if (transition == Geofence.GEOFENCE_TRANSITION_ENTER) "ENTER" else "EXIT"
        val ts = System.currentTimeMillis()

        // Use goAsync() to allow BroadcastReceiver to handle coroutine work
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dao = database.geofenceEventDao()
                ids.forEach { id ->
                    dao.insert(
                        GeofenceEventEntity(
                            geofenceId = id,
                            transition = transitionLabel,
                            timestamp = ts
                        )
                    )
                }
                Timber.d("Persisted ${ids.size} geofence events")
            } catch (e: Exception) {
                Timber.e(e, "Failed to persist geofence events")
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun showNotification(context: Context, transition: Int, ids: String) {
        // Check POST_NOTIFICATIONS permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Timber.w("POST_NOTIFICATIONS permission not granted, skipping geofence notification")
                return
            }
        }

        val channelId = AppConstants.NotificationChannels.GEOFENCE
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(channelId, context.getString(R.string.location_alerts), NotificationManager.IMPORTANCE_HIGH)
        manager.createNotificationChannel(channel)

        val action = if (transition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            context.getString(R.string.geofence_entered, ids)
        } else {
            context.getString(R.string.geofence_exited, ids)
        }
        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(context.getString(R.string.location_alert))
            .setContentText(action)
            .setSmallIcon(R.drawable.ic_map)
            .build()

        manager.notify(AppConstants.NotificationIds.GEOFENCE, notification)
    }
}

