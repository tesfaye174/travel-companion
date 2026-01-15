package com.example.travelcompanion

import android.app.Application
import androidx.work.*
import com.example.travelcompanion.data.db.AppDatabase
import com.example.travelcompanion.data.repository.TravelRepositoryImpl
import com.example.travelcompanion.domain.repository.TravelRepository
import com.example.travelcompanion.utils.ReminderWorker
import java.util.concurrent.TimeUnit

class TravelCompanionApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository: TravelRepository by lazy { 
        TravelRepositoryImpl(database.tripDao(), database.journeyDao()) 
    }

    override fun onCreate() {
        super.onCreate()
        scheduleReminder()
    }

    private fun scheduleReminder() {
        val request = PeriodicWorkRequestBuilder<ReminderWorker>(1, TimeUnit.DAYS)
            .setConstraints(Constraints.Builder().setRequiresBatteryNotLow(true).build())
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "trip_reminder",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
}
