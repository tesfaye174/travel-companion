package com.travelcompanion.domain.usecase

import com.travelcompanion.domain.model.Trip
import com.travelcompanion.domain.model.TripValidationUtils
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
        TripValidationUtils.validateForCreate(trip)
        return repository.insertTrip(trip)
    }
}
