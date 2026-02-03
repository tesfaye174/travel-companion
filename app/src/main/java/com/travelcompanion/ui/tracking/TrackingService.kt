package com.travelcompanion.ui.tracking

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.IBinder
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import com.travelcompanion.R
import com.travelcompanion.domain.repository.ITripRepository
import com.travelcompanion.utils.AppConstants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import timber.log.Timber
import javax.inject.Inject
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.gson.Gson
import java.io.File
import java.io.FileWriter
import com.travelcompanion.workers.SaveJourneyWorker

@AndroidEntryPoint
class TrackingService : Service() {

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var repository: ITripRepository

    @Inject
    lateinit var locationProvider: com.travelcompanion.location.LocationProvider

    // coroutine scope tied to service lifecycle
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var currentTripId: Long = -1
    private var coordinates = mutableListOf<com.travelcompanion.domain.model.Coordinate>()
    private var startTime: Long = 0

    // DB id del journey checkpoint (0 se non ancora creato)
    private var currentJourneyDbId: Long = 0L

    private val notificationId = AppConstants.Tracking.TRACKING_NOTIFICATION_ID
    private val channelId = AppConstants.Tracking.TRACKING_CHANNEL_ID

    // soglia checkpoint: salva parziale ogni N coordinate
    private val CHECKPOINT_EVERY = 30

    override fun onCreate() {
        super.onCreate()
        // locationProvider is provided by Hilt (Play Services or Platform implementation)
        createNotificationChannel()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.getLongExtra(EXTRA_TRIP_ID, -1)?.let { tripId ->
            currentTripId = tripId
            startTime = System.currentTimeMillis()
            startTracking()

            startForeground(notificationId, createTrackingNotification())
        }

        return START_STICKY
    }

    private fun startTracking() {
        // Delegate to LocationProvider implementation
        if (!locationProvider.hasLocationPermission(this)) {
            stopSelf()
            return
        }

        locationProvider.startLocationUpdates(
            onLocation = { location ->
                serviceScope.launch { saveLocation(location) }
            },
            onAvailabilityChanged = { available ->
                if (!available) Timber.w("Location services unavailable")
            }
        )
    }

    private suspend fun saveLocation(location: Location) = withContext(Dispatchers.IO) {
        val coordinate = com.travelcompanion.domain.model.Coordinate(
            latitude = location.latitude,
            longitude = location.longitude,
            timestamp = java.util.Date()
        )
        coordinates.add(coordinate)

        // Calculate distance
        val totalDistance = calculateTotalDistance()

        // Update notification
        updateTrackingNotification(totalDistance)

        // Broadcast update
        sendLocationUpdate(location, totalDistance)

        // Checkpoint: ogni CHECKPOINT_EVERY coordinate salva parziale nel DB
        if (coordinates.size % CHECKPOINT_EVERY == 0) {
            // crea o aggiorna un Journey parziale
            val partial = com.travelcompanion.domain.model.Journey(
                id = currentJourneyDbId,
                tripId = currentTripId,
                startTime = java.util.Date(startTime),
                endTime = java.util.Date(),
                distance = totalDistance,
                coordinates = coordinates.toList()
            )

            try {
                val savedId = repository.insertJourney(partial)
                if (currentJourneyDbId == 0L) currentJourneyDbId = savedId
                Timber.d("Checkpoint saved journey id=$savedId coords=${coordinates.size}")
            } catch (e: Exception) {
                Timber.e(e, "Failed to save checkpoint journey")
            }
        }
    }

    override fun onDestroy() {
        // Prima schedulo il salvataggio, poi cancello il scope per evitare che i dati
        // in memoria vengano persi prima della schedulazione
        stopTracking()
        super.onDestroy()
        serviceScope.cancel()
    }

    private fun stopTracking() {
        locationProvider.stopLocationUpdates()

        // Schedule save via WorkManager instead of launching a NonCancellable coroutine
        // to ensure the work runs even if the service/app process is killed.
        if (currentTripId != -1L && coordinates.isNotEmpty()) {
            // se abbiamo un journey checkpoint in DB passiamo solo l'id
            try {
                val workDataBuilder = androidx.work.Data.Builder()

                if (currentJourneyDbId != 0L) {
                    workDataBuilder.putLong(SaveJourneyWorker.KEY_JOURNEY_DB_ID, currentJourneyDbId)
                } else {
                    // fallback: serializza l'intero journey (se piccolo)
                    val journey = com.travelcompanion.domain.model.Journey(
                        tripId = currentTripId,
                        startTime = java.util.Date(startTime),
                        endTime = java.util.Date(),
                        distance = calculateTotalDistance(),
                        coordinates = coordinates.toList()
                    )

                    val gson = com.google.gson.Gson()
                    val json = gson.toJson(journey)

                    if (json.toByteArray().size < 9000) {
                        workDataBuilder.putString(SaveJourneyWorker.KEY_JOURNEY_JSON, json)
                    } else {
                        val file = java.io.File.createTempFile("journey_", ".json", cacheDir)
                        java.io.FileWriter(file).use { fw -> fw.write(json) }
                        workDataBuilder.putString(SaveJourneyWorker.KEY_JOURNEY_FILE_PATH, file.absolutePath)
                    }
                }

                val request = androidx.work.OneTimeWorkRequestBuilder<com.travelcompanion.workers.SaveJourneyWorker>()
                    .setInputData(workDataBuilder.build())
                    .build()

                androidx.work.WorkManager.getInstance(this).enqueueUniqueWork(
                    "save_journey_${currentTripId}",
                    androidx.work.ExistingWorkPolicy.REPLACE,
                    request
                )
            } catch (e: Exception) {
                Timber.e(e, "Failed to schedule SaveJourneyWorker")
                // As a last resort, try to save synchronously in IO (best-effort)
                runBlocking(Dispatchers.IO) {
                    try {
                        saveCompleteJourney()
                    } catch (ex: Exception) {
                        Timber.e(ex, "Synchronous fallback save failed")
                    }
                }
            }
        }

        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(channelId, "Tracking", NotificationManager.IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(channel)
    }

    private fun createTrackingNotification(): Notification {
        return getNotificationBuilder("Tracking in progress").build()
    }

    private fun getNotificationBuilder(text: String): NotificationCompat.Builder {
         return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Tracking")
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_location)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
    }

    private fun updateTrackingNotification(distance: Float) {
        val text = "Distance: %.2f km".format(distance)
        notificationManager.notify(notificationId, getNotificationBuilder(text).build())
    }

    private fun calculateTotalDistance(): Float {
        if (coordinates.size < 2) return 0f
        var distance = 0f
        for (i in 0 until coordinates.size - 1) {
            val c1 = coordinates[i]
            val c2 = coordinates[i + 1]
            distance += com.travelcompanion.utils.LocationUtils.calculateDistance(
                c1.latitude, c1.longitude, c2.latitude, c2.longitude
            )
        }
        return distance
    }

    private fun sendLocationUpdate(location: Location, distance: Float) {
        val intent = Intent(ACTION_LOCATION_UPDATE)
        intent.setPackage(packageName)
        intent.putExtra("lat", location.latitude)
        intent.putExtra("lon", location.longitude)
        intent.putExtra("dist", distance)
        sendBroadcast(intent)
    }

    private suspend fun saveCompleteJourney() {
        if (currentTripId == -1L || coordinates.isEmpty()) return

        val journey = com.travelcompanion.domain.model.Journey(
            tripId = currentTripId,
            startTime = java.util.Date(startTime),
            endTime = java.util.Date(),
            distance = calculateTotalDistance(),
            coordinates = coordinates.toList()
        )
        try {
            repository.insertJourney(journey)
            recalculateAndPersistTripTotals(currentTripId)
        } catch (e: SecurityException) {
            Timber.e(e, "Permessi mancanti per il salvataggio del journey")
        } catch (e: IllegalStateException) {
            Timber.e(e, "Stato non valido durante il salvataggio del journey")
        } catch (e: Exception) {
            Timber.e(e, "Error saving journey")
        }
    }

    private suspend fun recalculateAndPersistTripTotals(tripId: Long) {
        val trip = repository.getTripById(tripId) ?: return
        val journeys = repository.getJourneysByTripId(tripId).first()

        val totalDistanceKm = journeys.sumOf { it.distance.toDouble() }.toFloat()
        val totalDurationMs = journeys.sumOf { j ->
            val end = j.endTime?.time ?: return@sumOf 0L
            val start = j.startTime.time
            (end - start).coerceAtLeast(0L)
        }

        repository.updateTrip(
            trip.copy(
                totalDistance = totalDistanceKm,
                totalDuration = totalDurationMs,
                isTracking = false
            )
        )
    }

    companion object {
        const val EXTRA_TRIP_ID = "extra_trip_id"
        const val ACTION_LOCATION_UPDATE = "com.travelcompanion.LOCATION_UPDATE"
    }
}
