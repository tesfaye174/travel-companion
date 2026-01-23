package com.travelcompanion.domain.usecase

import com.travelcompanion.domain.model.Trip
import com.travelcompanion.domain.repository.ITripRepository
import javax.inject.Inject

/**
 * Use case per recuperare un viaggio specifico tramite il suo ID.
 * 
 * Incapsula la logica per il recupero di un singolo viaggio,
 * fornendo un'API pulita per il layer di presentazione.
 */
class GetTripByIdUseCase @Inject constructor(
    private val repository: ITripRepository
) {
    /**
     * Esegue il use case per ottenere un viaggio tramite ID.
     * @param tripId L'identificatore univoco del viaggio
     * @return Il viaggio se trovato, null altrimenti
     */
    suspend operator fun invoke(tripId: Long): Trip? {
        return repository.getTripById(tripId)
    }
}
