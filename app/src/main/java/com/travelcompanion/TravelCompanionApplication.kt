package com.travelcompanion

import android.app.Application
import android.content.pm.ApplicationInfo
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.travelcompanion.utils.NotificationUtils
import com.travelcompanion.ui.worker.ReminderWorker
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * Application class - entry point for Hilt DI.
 * Sets up Timber logging and schedules daily reminder worker.
 */
@HiltAndroidApp
class TravelCompanionApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // setup Timber for debug builds only
        val isDebuggable = (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        if (isDebuggable) {
            Timber.plant(Timber.DebugTree())
        }

        NotificationUtils.createNotificationChannel(this)
        schedulePeriodicReminder()
    }

    private fun schedulePeriodicReminder() {
        // runs once a day to check if user wants trip reminders
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
