package com.travelcompanion.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.travelcompanion.domain.model.Journey
import com.travelcompanion.domain.repository.ITripRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.first
import timber.log.Timber
import java.io.File

@HiltWorker
class SaveJourneyWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val repository: ITripRepository
) : CoroutineWorker(context, params) {

    companion object {
        const val KEY_JOURNEY_JSON = "key_journey_json"
        const val KEY_JOURNEY_FILE_PATH = "key_journey_file_path"
        const val KEY_JOURNEY_DB_ID = "key_journey_db_id"
    }

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                val gson = Gson()

                // Se è stato passato un id DB, finalizza quel journey
                val dbId = inputData.getLong(KEY_JOURNEY_DB_ID, 0L)
                if (dbId != 0L) {
                    val existing = repository.getJourneyById(dbId)
                    if (existing == null) {
                        Timber.w("No existing journey with id=$dbId")
                        return@withContext Result.failure()
                    }

                    // aggiorna campi finali basati sugli ultimi dati (se presenti nel DB
                    // l'existing potrebbe già contenere coordinates parziali)
                    val journeys = repository.getJourneysByTripId(existing.tripId).first()

                    val totalDistanceKm = journeys.sumOf { j: Journey -> j.distance.toDouble() }.toFloat()
                    val totalDurationMs = journeys.sumOf { j: Journey ->
                        val end = j.endTime?.time ?: 0L
                        val start = j.startTime.time
                        (end - start).coerceAtLeast(0L)
                    }

                    val trip = repository.getTripById(existing.tripId) ?: return@withContext Result.failure()

                    repository.updateTrip(
                        trip.copy(
                            totalDistance = totalDistanceKm,
                            totalDuration = totalDurationMs,
                            isTracking = false
                        )
                    )

                    // mark journey end time/distance by updating the journey itself
                    val finalized = existing.copy(
                        endTime = java.util.Date(),
                        distance = totalDistanceKm,
                        // coordinates left as-is (already present)
                    )

                    repository.updateJourney(finalized)

                    return@withContext Result.success()
                }

                // Fallback: JSON or file input
                val json = inputData.getString(KEY_JOURNEY_JSON)?.takeIf { it.isNotBlank() }
                    ?: run {
                        val path = inputData.getString(KEY_JOURNEY_FILE_PATH)
                        if (path.isNullOrBlank()) null else File(path).takeIf { it.exists() }?.readText()
                    }

                if (json.isNullOrBlank()) {
                    Timber.w("No journey data found in Work input")
                    return@withContext Result.failure()
                }

                val journey = gson.fromJson(json, Journey::class.java)

                // salva il journey usando il repository iniettato
                repository.insertJourney(journey)

                // ricalcola totals e aggiorna trip
                val trip = repository.getTripById(journey.tripId) ?: return@withContext Result.failure()
                val journeys = repository.getJourneysByTripId(journey.tripId).first()

                val totalDistanceKm = journeys.sumOf { j: Journey -> j.distance.toDouble() }.toFloat()
                val totalDurationMs = journeys.sumOf { j: Journey ->
                    val end = j.endTime?.time ?: 0L
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

                // attempt cleanup of temp file if provided
                inputData.getString(KEY_JOURNEY_FILE_PATH)?.let { path ->
                    try { File(path).delete() } catch (e: Exception) { /* ignore */ }
                }

                Result.success()
            } catch (e: Exception) {
                Timber.e(e, "Error while saving journey in SaveJourneyWorker")
                // Ritorna retry per errori transitori
                Result.retry()
            }
        }
    }
}
