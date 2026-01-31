package com.travelcompanion.boot

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.travelcompanion.workers.NotificationWorker
import org.osmdroid.config.Configuration
import java.util.concurrent.TimeUnit

object AppInitializer {

    fun init(context: Context) {
        setupOsm(context)
        scheduleWorkers(context)
    }

    private fun setupOsm(context: Context) {
        // Configurazione obbligatoria per OSM
        Configuration.getInstance().load(context, context.getSharedPreferences("osm_prefs", Context.MODE_PRIVATE))
        Configuration.getInstance().userAgentValue = "TravelCompanion/1.0"
    }

    private fun scheduleWorkers(context: Context) {
        // Schedule Notification Worker ogni 24 ore
        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(1, TimeUnit.DAYS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "daily_travel_reminder",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }
}
