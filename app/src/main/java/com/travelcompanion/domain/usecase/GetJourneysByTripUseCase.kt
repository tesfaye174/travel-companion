package com.travelcompanion.domain.usecase

import com.travelcompanion.domain.model.Journey
import com.travelcompanion.domain.repository.ITripRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// UseCase to get journey segments for a trip
class GetJourneysByTripUseCase @Inject constructor(
    private val repository: ITripRepository
) {
    operator fun invoke(tripId: Long): Flow<List<Journey>> {
        require(tripId > 0) { "Trip ID must be positive" }
        return repository.getJourneysByTripId(tripId)
    }
}
