package com.travelcompanion.workers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.travelcompanion.R
import com.travelcompanion.ui.MainActivity
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        // Logica: Controlla l'ultima attività. Se > 7 giorni, manda notifica
        // Per ora, manda una notifica demo come richiesto
        sendReminderNotification(context)
        return Result.success()
    }

    private fun sendReminderNotification(ctx: Context) {
        val channelId = "travel_reminders"

        val intent = Intent(ctx, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(ctx, channelId)
            .setContentTitle("Non hai registrato viaggi recentemente!")
            .setContentText("È ora di pianificare una nuova avventura?")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(
            NotificationChannel(channelId, "Reminders", NotificationManager.IMPORTANCE_DEFAULT)
        )
        manager.notify(100, notification)
    }
}
