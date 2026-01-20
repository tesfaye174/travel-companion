package com.travelcompanion

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.travelcompanion.utils.NotificationUtils
import com.travelcompanion.ui.worker.ReminderWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class TravelCompanionApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Create notification channel
        NotificationUtils.createNotificationChannel(this)

        schedulePeriodicReminder()
    }

    private fun schedulePeriodicReminder() {
        val constraints = Constraints.Builder().build()

        val work = PeriodicWorkRequestBuilder<ReminderWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "trip_reminder",
            ExistingPeriodicWorkPolicy.UPDATE,
            work
        )
    }
}

