package com.travelcompanion.workers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.travelcompanion.R
import com.travelcompanion.data.db.AppDatabase
import com.travelcompanion.location.GeofenceProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import timber.log.Timber

/**
 * Worker che ri-registra le geofence al boot o dopo che il sistema le ha cancellate.
 * Usa setForeground() perché la re-registrazione può richiedere più di pochi secondi
 * e WorkManager richiederebbe altrimenti un foreground service esplicito su Android 12+.
 */
@HiltWorker
class GeofenceRegistrationWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val database: AppDatabase,
    private val geofenceProvider: GeofenceProvider
) : CoroutineWorker(appContext, params) {

    companion object {
        const val CHANNEL_ID = "geofence_worker_channel"
        const val NOTIFICATION_ID = 9876
    }

    private fun createNotification(): Notification {
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            CHANNEL_ID,
            applicationContext.getString(R.string.geofence_registration_channel),
            NotificationManager.IMPORTANCE_LOW
        )
        manager.createNotificationChannel(channel)

        return NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(applicationContext.getString(R.string.app_name))
            .setContentText(applicationContext.getString(R.string.geofence_registering))
            .setSmallIcon(R.drawable.ic_map)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    override suspend fun doWork(): Result {
        return try {
            setForeground(ForegroundInfo(NOTIFICATION_ID, createNotification()))

            val areas = database.geofenceAreaDao().getAll().first()
            if (areas.isEmpty()) return Result.success()

            areas.forEach { area ->
                geofenceProvider.addGeofence(area.id, area.latitude, area.longitude, area.radiusMeters)
            }

            Timber.d("GeofenceRegistrationWorker re-registered ${areas.size} geofences")
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "Error re-registering geofences on boot")
            // Result.retry() lascia a WorkManager il compito di ripianificare con backoff esponenziale
            Result.retry()
        }
    }
}
