package com.travelcompanion.domain.usecase

import com.travelcompanion.domain.model.Trip
import com.travelcompanion.domain.repository.ITripRepository
import javax.inject.Inject

/**
 * UseCase for creating a new trip.
 * Validates input before saving to db.
 */
class CreateTripUseCase @Inject constructor(
    private val repository: ITripRepository
) {
    suspend operator fun invoke(trip: Trip): Long {
        validateTrip(trip)
        return repository.insertTrip(trip)
    }

    private fun validateTrip(trip: Trip) {
        require(trip.title.isNotBlank()) { "Trip title cannot be empty" }
        require(trip.destination.isNotBlank()) { "Destination cannot be empty" }
        trip.endDate?.let { endDate ->
            require(endDate >= trip.startDate) { "End date must be after start date" }
        }
    }
}
