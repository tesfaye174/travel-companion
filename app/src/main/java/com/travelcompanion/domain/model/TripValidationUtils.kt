package com.travelcompanion.domain.model


/**
 * Utility per la validazione dei dati dei viaggi.
 *
 * Centralizzare la validazione qui invece che nei ViewModel o UseCase
 * garantisce consistenza: le stesse regole vengono applicate ovunque.
 *
 * Uso require() invece di if/throw perché:
 * - È più conciso
 * - Genera automaticamente IllegalArgumentException
 * - Il messaggio nel lambda viene valutato solo se la condizione fallisce
 */
object TripValidationUtils {

    /**
     * Valida un viaggio prima della creazione.
     * Esempio di commento didattico: questa funzione centralizza la logica di validazione,
     * garantendo che le stesse regole siano applicate ovunque nell'applicazione.
     * Lancia IllegalArgumentException se i dati non sono validi.
     *
     * @throws IllegalArgumentException se:
     * - Il titolo è vuoto o contiene solo spazi
     * - La destinazione è vuota
     * - La data di fine è precedente alla data di inizio
     */
    fun validateForCreate(trip: Trip) {
        require(trip.title.isNotBlank()) { "Il titolo del viaggio non può essere vuoto" }
        require(trip.destination.isNotBlank()) { "La destinazione non può essere vuota" }
        trip.endDate?.let { endDate ->
            require(endDate >= trip.startDate) { "La data di fine deve essere dopo la data di inizio" }
        }
    }

    /**
     * Valida un viaggio prima dell'aggiornamento.
     * Esempio di commento didattico: la funzione verifica che l'ID sia valido e che i campi obbligatori siano compilati.
     *
     * Richiede anche che l'ID sia valido (> 0).
     */
    fun validateForUpdate(trip: Trip) {
        require(trip.id > 0) { "Il viaggio deve avere un ID valido per l'aggiornamento" }
        require(trip.title.isNotBlank()) { "Il titolo non può essere vuoto" }
        require(trip.destination.isNotBlank()) { "La destinazione non può essere vuota" }
    }

    /**
     * Valida un viaggio prima della cancellazione.
     * Esempio di commento didattico: la funzione verifica solo la validità dell'ID.
     *
     * Verifica solo che l'ID sia valido.
     */
    fun validateForDelete(trip: Trip) {
        require(trip.id > 0) { "Il viaggio deve avere un ID valido" }
    }
}
