package com.travelcompanion.domain.usecase

import com.travelcompanion.domain.model.Trip
import com.travelcompanion.domain.repository.ITripRepository
import javax.inject.Inject

/**
 * Use case per l'eliminazione di un viaggio.
 * 
 * Gestisce la logica di business per l'eliminazione dei viaggi,
 * inclusa qualsiasi pulizia che potrebbe essere necessaria.
 */
class DeleteTripUseCase @Inject constructor(
    private val repository: ITripRepository
) {
    /**
     * Esegue il use case per eliminare un viaggio.
     * @param trip Il viaggio da eliminare
     */
    suspend operator fun invoke(trip: Trip) {
        require(trip.id > 0) { "Il viaggio deve avere un ID valido per essere eliminato" }
        repository.deleteTrip(trip)
    }
}
