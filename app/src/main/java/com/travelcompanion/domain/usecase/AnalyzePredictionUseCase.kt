package com.travelcompanion.domain.usecase

import com.travelcompanion.domain.model.Trip

// Il domain non deve contenere testo della UI: il use case dice solo "che tipo"
// di messaggio mostrare, la stringa vera sta in strings.xml e la sceglie il Fragment
enum class PredictionMessage {
    NO_DATA,
    TIRELESS_TRAVELER,
    NEXT_MONTH_ESTIMATE
}

data class PredictionResult(
    val predictedKm: Double,
    val message: PredictionMessage
)

/**
 * UseCase che stima i km percorsi nel prossimo viaggio in base alla media storica,
 * applicando un fattore di crescita del 20% sulla media per trip.
 */
class AnalyzePredictionUseCase {
    fun execute(trips: List<Trip>): PredictionResult {
        if (trips.isEmpty()) return PredictionResult(0.0, PredictionMessage.NO_DATA)

        val totalKm = trips.sumOf { it.totalDistance.toDouble() }
        val avgKmPerTrip = totalKm / trips.size

        // +20% sulla media storica come stima ottimistica del prossimo viaggio
        val predicted = avgKmPerTrip * 1.2

        val msg = if (predicted > 100) PredictionMessage.TIRELESS_TRAVELER
        else PredictionMessage.NEXT_MONTH_ESTIMATE

        return PredictionResult(predicted, msg)
    }
}
