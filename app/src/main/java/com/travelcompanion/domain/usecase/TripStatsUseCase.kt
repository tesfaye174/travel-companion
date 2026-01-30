package com.travelcompanion.domain.usecase

import com.travelcompanion.domain.repository.ITripRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

// calculates trip statistics
class TripStatsUseCase @Inject constructor(
    private val repository: ITripRepository
) {
    suspend operator fun invoke(): TravelStatistics {
        val totalDistance = repository.getTotalDistance()
        val totalDuration = repository.getTotalDuration()
        val tripCount = repository.getTripCount()
        val monthlyStats = repository.getMonthlyStats()
        val tripTypeStats = repository.getTripTypeStats()

        // Calculate total photos from all trips
        val allTrips = repository.getAllTrips()
        val totalPhotos = try {
            allTrips.first().sumOf { it.photoCount }
        } catch (e: Exception) {
            0
        }

        return TravelStatistics(
            totalDistanceKm = totalDistance,
            totalDurationMs = totalDuration,
            totalTrips = tripCount,
            totalPhotos = totalPhotos,
            averageDistancePerTrip = if (tripCount > 0) totalDistance / tripCount else 0f,
            averageDurationPerTrip = if (tripCount > 0) totalDuration / tripCount else 0L,
            monthlyStats = monthlyStats,
            tripTypeStats = tripTypeStats
        )
    }

    // aggregated stats data
    data class TravelStatistics(
        val totalDistanceKm: Float,
        val totalDurationMs: Long,
        val totalTrips: Int,
        val totalPhotos: Int,
        val averageDistancePerTrip: Float,
        val averageDurationPerTrip: Long,
        val monthlyStats: List<com.travelcompanion.domain.model.MonthlyStat>,
        val tripTypeStats: List<com.travelcompanion.domain.model.TripTypeStat>
    ) {
        val totalDurationHours: Float
            get() = totalDurationMs / (1000f * 60f * 60f)
    }
}
