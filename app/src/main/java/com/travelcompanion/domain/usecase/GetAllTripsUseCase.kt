package com.travelcompanion.domain.usecase

import com.travelcompanion.domain.model.Trip
import com.travelcompanion.domain.repository.ITripRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case per recuperare tutti i viaggi dal repository.
 * 
 * Questo use case incapsula la logica di business per il recupero dei viaggi,
 * seguendo il principio Clean Architecture di separazione delle responsabilità.
 * Il ViewModel dovrebbe usare questo invece di accedere direttamente al repository.
 */
class GetAllTripsUseCase @Inject constructor(
    private val repository: ITripRepository
) {
    /**
     * Esegue il use case per ottenere tutti i viaggi.
     * @return Flow che emette la lista di tutti i viaggi, ordinati per data inizio decrescente
     */
    operator fun invoke(): Flow<List<Trip>> {
        return repository.getAllTrips()
    }
}
