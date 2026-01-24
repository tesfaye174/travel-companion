package com.travelcompanion.domain.usecase

import com.travelcompanion.domain.model.Trip
import com.travelcompanion.domain.repository.ITripRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// UseCase to get all trips from repo
class GetAllTripsUseCase @Inject constructor(
    private val repository: ITripRepository
) {
    operator fun invoke(): Flow<List<Trip>> {
        return repository.getAllTrips()
    }
}
