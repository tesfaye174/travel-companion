package com.travelcompanion.domain.usecase

import com.travelcompanion.domain.model.Trip
import com.travelcompanion.domain.repository.ITripRepository
import javax.inject.Inject

// UseCase to update existing trip
class UpdateTripUseCase @Inject constructor(
    private val repository: ITripRepository
) {
    suspend operator fun invoke(trip: Trip) {
        validateTrip(trip)
        repository.updateTrip(trip)
    }

    private fun validateTrip(trip: Trip) {
        require(trip.id > 0) { "Trip must have valid ID for update" }
        require(trip.title.isNotBlank()) { "Title cannot be empty" }
        require(trip.destination.isNotBlank()) { "Destination cannot be empty" }
    }
}
