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
            // Nota per lo studente: doWork gira in background e deve essere resiliente.
            // Qui gestiamo sia il caso in cui viene passato un DB id (finalizza journey)
            // sia il caso in cui il journey arriva come JSON/file temporaneo.
            try {
                val gson = Gson()

                // Se è stato passato un id DB, finalizza quel journey: aggiorna i dati finali (endTime, distance, coordinates) nel database.
                // Questo garantisce che il journey sia completo e coerente anche in caso di salvataggi parziali.
                val dbId = inputData.getLong(KEY_JOURNEY_DB_ID, 0L)
                if (dbId != 0L) {
                    val existing = repository.getJourneyById(dbId)
                    if (existing == null) {
                        Timber.w("No existing journey with id=$dbId")
                        return@withContext Result.failure()
                    }

                    // Aggiorna i campi finali del journey in base agli ultimi dati disponibili nel DB.
                    // L'oggetto existing potrebbe già contenere coordinate parziali se il salvataggio è stato fatto in più step.
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

                    // Segna la fine del journey aggiornando endTime e distance nel database.
                    val finalized = existing.copy(
                        endTime = java.util.Date(),
                        distance = totalDistanceKm,
                        // Le coordinate vengono lasciate invariate se già presenti (già salvate in step precedenti).
                    )

                    repository.updateJourney(finalized)

                    return@withContext Result.success()
                }

                // Fallback: se non è stato passato un id DB, si usa l'input JSON o file per ricostruire il journey.
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

                // Salva il journey usando il repository iniettato tramite Hilt.
                repository.insertJourney(journey)

                // Ricalcola i totali (distance, duration) e aggiorna il trip associato.
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

                // Prova a eliminare il file temporaneo se è stato usato per il salvataggio.
                inputData.getString(KEY_JOURNEY_FILE_PATH)?.let { path ->
                    try {
                        File(path).delete()
                    } catch (e: Exception) {
                        Timber.w(e, "Failed to delete temporary journey file: %s", path)
                    }
                }

                Result.success()
            } catch (e: Exception) {
                Timber.e(e, "Error while saving journey in SaveJourneyWorker")
                // Ritorna retry per errori transitori (es. DB lock, IO temporanei) per garantire robustezza del worker.
                Result.retry()
            }
        }
    }
}
