package com.travelcompanion.domain.usecase

import com.travelcompanion.domain.model.Trip
import com.travelcompanion.domain.model.TripType
import com.travelcompanion.domain.repository.ITripRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// UseCase to get trips filtered by type
class GetTripsByTypeUseCase @Inject constructor(
    private val repository: ITripRepository
) {
    operator fun invoke(type: TripType): Flow<List<Trip>> {
        return repository.getTripsByType(type)
    }
}
