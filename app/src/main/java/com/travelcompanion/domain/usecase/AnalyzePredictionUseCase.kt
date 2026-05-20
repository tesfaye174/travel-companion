package com.travelcompanion.domain.usecase

import com.travelcompanion.domain.model.LocationPoint
import com.travelcompanion.domain.model.Trip
import java.util.Locale

data class PredictionResult(
    val predictedKm: Double,
    val message: String
)

class AnalyzePredictionUseCase {
    fun execute(trips: List<Trip>, @Suppress("UNUSED_PARAMETER") locations: List<LocationPoint>): PredictionResult {
        if (trips.isEmpty()) return PredictionResult(0.0, "Not enough data available.")

        val totalKm = trips.sumOf { it.totalDistance.toDouble() }
        val avgKmPerTrip = totalKm / trips.size

        val predicted = avgKmPerTrip * 1.2

        val msg = if (predicted > 100) "You're a tireless traveler!"
        else "Next month you could travel ${String.format(Locale.getDefault(), "%.1f", predicted)} km."

        return PredictionResult(predicted, msg)
    }
}
