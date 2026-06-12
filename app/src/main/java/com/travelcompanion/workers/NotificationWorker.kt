package com.travelcompanion.workers

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.travelcompanion.R
import com.travelcompanion.utils.AppConstants
import com.travelcompanion.MainActivity
import com.travelcompanion.data.preferences.SettingsDataStore
import com.travelcompanion.domain.repository.ITripRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.Date

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val repository: ITripRepository,
    private val settingsDataStore: SettingsDataStore
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        // L'utente può disattivare i promemoria dalle impostazioni: in quel caso il worker
        // gira lo stesso (è periodico) ma non deve mandare niente
        if (!settingsDataStore.notifyRemindersFlow.first()) {
            return Result.success()
        }

        // Il promemoria viene inviato solo se l'utente non ha registrato viaggi negli ultimi 7 giorni
        val sevenDaysAgo = Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000L)
        val recentTrips = repository.getTripsBetweenDates(sevenDaysAgo, Date()).first()
        if (recentTrips.isEmpty()) {
            sendReminderNotification(context)
        }
        return Result.success()
    }

    private fun sendReminderNotification(ctx: Context) {
        // Su Android 13+ POST_NOTIFICATIONS è un permesso runtime: senza di esso la notifica non può essere inviata
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    ctx,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val channelId = AppConstants.NotificationChannels.REMINDERS

        val intent = Intent(ctx, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(ctx, channelId)
            .setContentTitle(ctx.getString(R.string.time_to_travel_title))
            .setContentText(ctx.getString(R.string.time_to_travel_text))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(
            NotificationChannel(channelId, ctx.getString(R.string.notify_reminders), NotificationManager.IMPORTANCE_DEFAULT)
        )
        manager.notify(AppConstants.NotificationIds.REMINDER, notification)
    }
}
