package com.travelcompanion

import android.app.Application
import android.content.pm.ApplicationInfo
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.travelcompanion.workers.NotificationWorker
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration as OsmConfig
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class TravelCompanionApplication : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory
    @Inject lateinit var database: com.travelcompanion.data.db.AppDatabase

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()

    override fun onCreate() {
        super.onCreate()

        val isDebuggable = (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        if (isDebuggable) {
            Timber.plant(Timber.DebugTree())
        }

        // Configure osmdroid once at application startup (was previously per-Fragment).
        // SharedPrefs read is small and is safer than races with MapFragment.setTileSource.
        OsmConfig.getInstance().load(
            applicationContext,
            getSharedPreferences("osmdroid", MODE_PRIVATE)
        )
        OsmConfig.getInstance().userAgentValue = packageName

        if (!BuildConfig.DEBUG) {
            schedulePeriodicReminder()
        }

        seedDemoDataIfNeeded()
    }

    private fun schedulePeriodicReminder() {
        val constraints = Constraints.Builder().build()

        val work = PeriodicWorkRequestBuilder<NotificationWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "trip_reminder",
            ExistingPeriodicWorkPolicy.UPDATE,
            work
        )
    }

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private fun seedDemoDataIfNeeded() {
        if (!BuildConfig.DEBUG) return

        appScope.launch {
            try {
                val count = database.tripDao().getTripCount()
                if (count == 0) {
                    val now = System.currentTimeMillis()
                    val demoTrips = listOf(
                        com.travelcompanion.data.db.entities.TripEntity(
                            title = "Demo: Roma",
                            destination = "Rome, IT",
                            tripType = com.travelcompanion.domain.model.TripType.LOCAL,
                            startDate = now - 86400000L * 7,
                            endDate = now - 86400000L * 6,
                            totalDistance = 12.3f,                  // km (matches TripsAdapter format)
                            totalDuration = 2 * 60 * 60 * 1000L,    // 2h in millis (matches TrackingService)
                            photoCount = 0                          // no PhotoNote entities seeded
                        ),
                        com.travelcompanion.data.db.entities.TripEntity(
                            title = "Demo: Torino",
                            destination = "Torino, IT",
                            tripType = com.travelcompanion.domain.model.TripType.DAY_TRIP,
                            startDate = now - 86400000L * 30,
                            endDate = now - 86400000L * 29,
                            totalDistance = 45.0f,                  // km
                            totalDuration = 4 * 60 * 60 * 1000L,    // 4h in millis
                            photoCount = 0
                        )
                    )
                    database.tripDao().insertTrips(demoTrips)
                    Timber.i("Inserted demo trips for fast demo startup")
                }
            } catch (e: Exception) {
                Timber.w(e, "seedDemoDataIfNeeded failed")
            }
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        appScope.cancel()
    }
}
