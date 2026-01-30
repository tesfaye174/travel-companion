package com.travelcompanion.domain.usecase

import com.travelcompanion.domain.model.Trip
import com.travelcompanion.domain.repository.ITripRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// UseCase to get single trip by ID
class GetTripByIdUseCase @Inject constructor(
    private val repository: ITripRepository
) {
    operator fun invoke(tripId: Long): Flow<Trip?> {
        return repository.getTripById(tripId)
    }
}
