package com.example.travelcompanion.ui.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.travelcompanion.data.db.AppDatabase
import com.example.travelcompanion.utils.NotificationUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar

class ReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                // Check if user hasn't created a trip recently
                val database = AppDatabase.getDatabase(applicationContext)
                val tripDao = database.tripDao()

                val calendar = Calendar.getInstance()
                calendar.add(Calendar.DAY_OF_YEAR, -7) // Last 7 days
                val weekAgo = calendar.timeInMillis

                val recentTrips = tripDao.getTripsBetweenDates(weekAgo, System.currentTimeMillis())

                // If no trips in the last week, send reminder
                if (recentTrips.isEmpty()) {
                    NotificationUtils.showReminderNotification(applicationContext)
                }

                Result.success()
            } catch (e: Exception) {
                Result.failure()
            }
        }
    }
}