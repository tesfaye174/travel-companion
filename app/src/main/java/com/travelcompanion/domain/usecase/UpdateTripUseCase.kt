package com.travelcompanion.domain.usecase

import com.travelcompanion.domain.model.Trip
import com.travelcompanion.domain.repository.ITripRepository
import javax.inject.Inject

/**
 * Use case per l'aggiornamento di un viaggio esistente.
 * 
 * Gestisce la logica di business per la modifica dei dati del viaggio,
 * garantendo l'integrità dei dati prima della persistenza.
 */
class UpdateTripUseCase @Inject constructor(
    private val repository: ITripRepository
) {
    /**
     * Esegue il use case per aggiornare un viaggio esistente.
     * @param trip Il viaggio con i dati aggiornati
     * @throws IllegalArgumentException se i dati del viaggio non sono validi
     */
    suspend operator fun invoke(trip: Trip) {
        validateTrip(trip)
        repository.updateTrip(trip)
    }

    private fun validateTrip(trip: Trip) {
        require(trip.id > 0) { "Il viaggio deve avere un ID valido per essere aggiornato" }
        require(trip.title.isNotBlank()) { "Il titolo del viaggio non può essere vuoto" }
        require(trip.destination.isNotBlank()) { "La destinazione del viaggio non può essere vuota" }
    }
}
