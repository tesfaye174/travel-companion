package com.travelcompanion.domain.usecase

import com.travelcompanion.domain.model.Trip
import com.travelcompanion.domain.model.TripValidationUtils
import com.travelcompanion.domain.repository.ITripRepository
import javax.inject.Inject

// UseCase to delete a trip
class DeleteTripUseCase @Inject constructor(
    private val repository: ITripRepository
) {
    suspend operator fun invoke(trip: Trip) {
        TripValidationUtils.validateForDelete(trip)
        repository.deleteTrip(trip)
    }
}
