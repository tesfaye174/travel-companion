package com.travelcompanion.workers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.work.ForegroundInfo
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.travelcompanion.data.db.AppDatabase
import com.travelcompanion.location.PlayServicesGeofenceProvider
import com.travelcompanion.location.PlatformGeofenceProvider
import com.travelcompanion.utils.AppConstants
import kotlinx.coroutines.flow.first
import timber.log.Timber

class GeofenceRegistrationWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {

    companion object {
        const val CHANNEL_ID = "geofence_worker_channel"
        const val NOTIFICATION_ID = 9876
    }

    private fun createNotification(): Notification {
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Geofence registration", NotificationManager.IMPORTANCE_LOW)
            manager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle("Travel Companion")
            .setContentText("Registering geofences...")
            .setSmallIcon(com.travelcompanion.R.drawable.ic_map)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    override suspend fun doWork(): Result {
        try {
            // Run as foreground to increase likelihood of completion on boot
            setForeground(ForegroundInfo(NOTIFICATION_ID, createNotification()))

            val db = AppDatabase.getDatabase(applicationContext)
            val areas = db.geofenceAreaDao().getAll().first()

            if (areas.isEmpty()) return Result.success()

            if (com.travelcompanion.BuildConfig.USE_PLAY_SERVICES_LOCATION) {
                val provider = PlayServicesGeofenceProvider(applicationContext)
                areas.forEach { area ->
                    provider.addGeofence(area.id, area.latitude, area.longitude, area.radiusMeters)
                }
            } else {
                val provider = PlatformGeofenceProvider(applicationContext, db)
                areas.forEach { area ->
                    provider.addGeofence(area.id, area.latitude, area.longitude, area.radiusMeters)
                }
            }

            Timber.d("GeofenceRegistrationWorker re-registered ${areas.size} geofences")
            return Result.success()
        } catch (e: Exception) {
            Timber.e(e, "Error re-registering geofences on boot")
            return Result.retry()
        }
    }
}
