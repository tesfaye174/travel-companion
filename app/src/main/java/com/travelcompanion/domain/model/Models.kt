package com.travelcompanion.domain.model

import java.util.Date

/**
 * Rappresenta un viaggio con tutti i suoi metadati.
 * 
 * Un viaggio è l'entità principale dell'app Travel Companion, contenente
 * informazioni su destinazione, date, tipo e statistiche aggregate.
 * 
 * @property id Identificatore univoco (auto-generato)
 * @property title Nome del viaggio definito dall'utente
 * @property destination Luogo o città di destinazione
 * @property tripType Classificazione del viaggio (locale, giornaliero, multi-giorno)
 * @property startDate Data di inizio del viaggio
 * @property endDate Data di fine del viaggio (null se in corso)
 * @property totalDistance Distanza totale percorsa in chilometri
 * @property totalDuration Tempo totale di viaggio in millisecondi
 * @property photoCount Numero di foto allegate al viaggio
 * @property notes Note dell'utente sul viaggio
 * @property isTracking Indica se il tracciamento GPS è attivo
 */
data class Trip(
    val id: Long = 0,
    val title: String,
    val destination: String,
    val tripType: TripType,
    val startDate: Date,
    val endDate: Date?,
    val totalDistance: Float = 0f,
    val totalDuration: Long = 0,
    val photoCount: Int = 0,
    val notes: String = "",
    val isTracking: Boolean = false
)

/**
 * Enumerazione dei tipi di viaggio per la classificazione.
 */
enum class TripType {
    /** Viaggi brevi nella zona locale */
    LOCAL,
    /** Escursioni di un giorno */
    DAY_TRIP,
    /** Viaggi estesi di più giorni */
    MULTI_DAY
}

/**
 * Rappresenta un segmento di percorso registrato durante un viaggio.
 * 
 * Un journey contiene le coordinate GPS raccolte durante il tracciamento attivo,
 * rappresentando un percorso continuo dall'inizio alla fine.
 * 
 * @property id Identificatore univoco
 * @property tripId ID del viaggio padre a cui appartiene
 * @property startTime Quando è iniziato il tracciamento
 * @property endTime Quando è terminato il tracciamento
 * @property distance Distanza coperta in questo segmento
 * @property coordinates Lista dei punti GPS lungo il percorso
 */
data class Journey(
    val id: Long = 0,
    val tripId: Long,
    val startTime: Date,
    val endTime: Date?,
    val distance: Float = 0f,
    val coordinates: List<Coordinate> = emptyList()
)

/**
 * Un singolo punto di coordinate GPS.
 * 
 * @property latitude Latitudine geografica
 * @property longitude Longitudine geografica
 * @property timestamp Quando è stata registrata questa coordinata
 */
data class Coordinate(
    val latitude: Double,
    val longitude: Double,
    val timestamp: Date = Date()
)

/**
 * Una foto con geolocalizzazione e nota opzionali.
 * 
 * @property id Identificatore univoco
 * @property tripId ID del viaggio padre a cui appartiene
 * @property imagePath Percorso del file immagine nel filesystem
 * @property note Descrizione della foto inserita dall'utente
 * @property latitude Latitudine geografica dove è stata scattata la foto
 * @property longitude Longitudine geografica dove è stata scattata la foto
 * @property timestamp Quando è stata scattata la foto
 */
data class PhotoNote(
    val id: Long = 0,
    val tripId: Long,
    val imagePath: String,
    val note: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val timestamp: Date = Date()
)

/**
 * Una nota testuale allegata a un viaggio, opzionalmente geolocalizzata.
 * 
 * @property id Identificatore univoco
 * @property tripId ID del viaggio padre a cui appartiene
 * @property title Titolo breve della nota
 * @property content Contenuto completo della nota
 * @property latitude Latitudine geografica (opzionale)
 * @property longitude Longitudine geografica (opzionale)
 * @property timestamp Quando è stata creata la nota
 * @property photoPath Percorso foto associata (opzionale)
 */
data class Note(
    val id: Long = 0,
    val tripId: Long,
    val title: String = "",
    val content: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val timestamp: Date = Date(),
    val photoPath: String? = null
)

/**
 * Un punto GPS dettagliato con dati di precisione e velocità.
 * 
 * Utilizzato per il tracciamento ad alta risoluzione con dati completi dei sensori.
 * 
 * @property id Identificatore univoco
 * @property journeyId ID del journey padre a cui appartiene
 * @property latitude Latitudine geografica
 * @property longitude Longitudine geografica
 * @property altitude Altitudine sul livello del mare (metri)
 * @property accuracy Raggio di precisione GPS (metri)
 * @property speed Velocità attuale (m/s)
 * @property timestamp Timestamp Unix in millisecondi
 */
data class LocationPoint(
    val id: Long = 0,
    val journeyId: Long,
    val latitude: Double,
    val longitude: Double,
    val altitude: Double? = null,
    val accuracy: Float? = null,
    val speed: Float? = null,
    val timestamp: Long
)

/**
 * Dettagli completi del viaggio inclusi tutti i dati correlati.
 * 
 * Aggrega un viaggio con i suoi percorsi, foto, note e statistiche
 * per la visualizzazione nelle schermate di dettaglio.
 * 
 * @property trip L'entità viaggio principale
 * @property journeys Tutti i segmenti di percorso del viaggio
 * @property photos Tutte le foto geolocalizzate
 * @property notes Tutte le note allegate
 * @property totalDistance Distanza totale calcolata
 * @property totalDuration Durata totale calcolata
 * @property locationPoints Punti GPS grezzi
 */
data class TripDetails(
    val trip: Trip,
    val journeys: List<Journey> = emptyList(),
    val photos: List<PhotoNote> = emptyList(),
    val notes: List<Note> = emptyList(),
    val totalDistance: Double = 0.0,
    val totalDuration: Long = 0,
    val locationPoints: List<LocationPoint> = emptyList()
)

// Backwards-compatible aliases for older code that referenced `Photo` and `Point`
typealias Photo = PhotoNote
typealias Point = LocationPoint

data class GeofenceArea(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val radiusMeters: Float
)

data class GeofenceEvent(
    val id: Long = 0,
    val geofenceId: String,
    val transition: String,
    val timestamp: Long
)

