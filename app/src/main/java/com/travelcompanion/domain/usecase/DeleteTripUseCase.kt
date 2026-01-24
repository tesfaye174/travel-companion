package com.travelcompanion.domain.usecase

import com.travelcompanion.domain.model.Trip
import com.travelcompanion.domain.repository.ITripRepository
import javax.inject.Inject

// UseCase to delete a trip
class DeleteTripUseCase @Inject constructor(
    private val repository: ITripRepository
) {
    suspend operator fun invoke(trip: Trip) {
        require(trip.id > 0) { "Trip must have valid ID" }
        repository.deleteTrip(trip)
    }
}
