package com.travelcompanion.domain.usecase

import com.travelcompanion.domain.repository.ITripRepository
import javax.inject.Inject

/**
 * Use case per recuperare le statistiche di viaggio aggregate.
 * 
 * Fornisce statistiche calcolate sulla cronologia dei viaggi dell'utente,
 * inclusi distanza totale, durata, conteggio viaggi e tendenze.
 */
class GetTravelStatisticsUseCase @Inject constructor(
    private val repository: ITripRepository
) {
    /**
     * Esegue il use case per ottenere statistiche di viaggio complete.
     * @return TravelStatistics contenente tutte le metriche calcolate
     */
    suspend operator fun invoke(): TravelStatistics {
        val totalDistance = repository.getTotalDistance()
        val totalDuration = repository.getTotalDuration()
        val tripCount = repository.getTripCount()
        val monthlyStats = repository.getMonthlyStats()
        val tripTypeStats = repository.getTripTypeStats()

        return TravelStatistics(
            totalDistanceKm = totalDistance,
            totalDurationMs = totalDuration,
            totalTrips = tripCount,
            averageDistancePerTrip = if (tripCount > 0) totalDistance / tripCount else 0f,
            averageDurationPerTrip = if (tripCount > 0) totalDuration / tripCount else 0L,
            monthlyStats = monthlyStats,
            tripTypeStats = tripTypeStats
        )
    }

    /**
     * Data class che rappresenta le statistiche di viaggio complete.
     */
    data class TravelStatistics(
        val totalDistanceKm: Float,
        val totalDurationMs: Long,
        val totalTrips: Int,
        val averageDistancePerTrip: Float,
        val averageDurationPerTrip: Long,
        val monthlyStats: List<ITripRepository.MonthlyStat>,
        val tripTypeStats: List<ITripRepository.TripTypeStat>
    ) {
        /**
         * Restituisce la durata totale formattata in ore.
         */
        val totalDurationHours: Float
            get() = totalDurationMs / (1000f * 60f * 60f)
    }
}
