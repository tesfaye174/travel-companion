package com.example.travelcompanion.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.travelcompanion.TravelCompanionApplication
import kotlinx.coroutines.flow.first

class ReminderWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val application = applicationContext as TravelCompanionApplication
        val repository = application.repository
        
        val trips = repository.getAllTrips().first()
        val lastTrip = trips.firstOrNull()

        val shouldRemind = lastTrip == null || 
                (System.currentTimeMillis() - lastTrip.startDate) > 3 * 24 * 60 * 60 * 1000 // 3 days

        if (shouldRemind) {
            showNotification()
        }

        return Result.success()
    }

    private fun showNotification() {
        val channelId = "reminder_channel"
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Trip Reminders", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("Planning a new adventure?")
            .setContentText("You haven't recorded a trip in a while. Start a new journey today!")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()

        notificationManager.notify(2, notification)
    }
}
