package com.travelcompanion.domain.usecase

import com.travelcompanion.domain.model.Trip
import com.travelcompanion.domain.repository.ITripRepository
import javax.inject.Inject

/**
 * Use case per la creazione di un nuovo viaggio.
 * 
 * Questo use case gestisce la logica di business per la creazione dei viaggi,
 * inclusa qualsiasi validazione necessaria prima della persistenza.
 */
class CreateTripUseCase @Inject constructor(
    private val repository: ITripRepository
) {
    /**
     * Esegue il use case per creare un nuovo viaggio.
     * @param trip Il viaggio da creare
     * @return L'ID del viaggio appena creato
     * @throws IllegalArgumentException se i dati del viaggio non sono validi
     */
    suspend operator fun invoke(trip: Trip): Long {
        validateTrip(trip)
        return repository.insertTrip(trip)
    }

    private fun validateTrip(trip: Trip) {
        require(trip.title.isNotBlank()) { "Il titolo del viaggio non può essere vuoto" }
        require(trip.destination.isNotBlank()) { "La destinazione del viaggio non può essere vuota" }
        trip.endDate?.let { endDate ->
            require(endDate >= trip.startDate) { "La data di fine deve essere successiva o uguale alla data di inizio" }
        }
    }
}
