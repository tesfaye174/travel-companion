package com.travelcompanion.domain.usecase

import com.travelcompanion.domain.model.Journey
import com.travelcompanion.domain.repository.ITripRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case per recuperare i percorsi associati a un viaggio specifico.
 * 
 * Un journey rappresenta un segmento tracciato del viaggio,
 * contenente coordinate GPS, distanza e dati sulla durata.
 */
class GetJourneysByTripUseCase @Inject constructor(
    private val repository: ITripRepository
) {
    /**
     * Esegue il use case per ottenere tutti i percorsi di un viaggio.
     * @param tripId L'ID del viaggio
     * @return Flow che emette la lista dei percorsi per il viaggio specificato
     */
    operator fun invoke(tripId: Long): Flow<List<Journey>> {
        require(tripId > 0) { "L'ID del viaggio deve essere positivo" }
        return repository.getJourneysByTripId(tripId)
    }
}
