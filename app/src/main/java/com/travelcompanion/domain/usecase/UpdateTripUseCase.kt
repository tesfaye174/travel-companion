package com.travelcompanion.domain.usecase

import com.travelcompanion.domain.model.Trip
import com.travelcompanion.domain.model.TripValidationUtils
import com.travelcompanion.domain.repository.ITripRepository
import javax.inject.Inject

// UseCase to update existing trip
class UpdateTripUseCase @Inject constructor(
    private val repository: ITripRepository
) {
    suspend operator fun invoke(trip: Trip) {
        TripValidationUtils.validateForUpdate(trip)
        repository.updateTrip(trip)
    }
}
