package com.travelcompanion.domain.model

import java.util.Date

/**
 * Modelli di dominio dell'applicazione Travel Companion.
 *
 * Questi sono i "domain models", cioè le classi che rappresentano i concetti
 * del dominio applicativo senza dipendere da dettagli implementativi come
 * Room o Retrofit. È una buona pratica del Clean Architecture.
 *
 * Differenza con le Entity:
 * - Entity (es. TripEntity) → mappate direttamente alle tabelle del database
 * - Domain Model (es. Trip) → usate nel resto dell'app (ViewModel, UseCase)
 *
 * La conversione tra i due avviene nel Repository tramite funzioni di estensione
 * toEntity() e toDomain() definite nel file di mapping.
 */

/**
 * Rappresenta un viaggio completo dell'utente.
 *
 * @property id ID univoco auto-generato dal database (0 per nuovi viaggi)
 * @property title Titolo del viaggio scelto dall'utente
 * @property destination Nome della destinazione (es. "Roma, Italia")
 * @property tripType Categoria del viaggio (locale, giornaliero, più giorni)
 * @property startDate Data di inizio del viaggio
 * @property endDate Data di fine (null se viaggio in corso o single day)
 * @property totalDistance Distanza totale percorsa in km
 * @property totalDuration Durata totale in millisecondi
 * @property photoCount Numero di foto scattate durante il viaggio
 * @property notes Eventuali note testuali dell'utente
 * @property isTracking True se il tracking GPS è attualmente attivo
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
 * Enum che definisce le categorie di viaggio disponibili.
 * Usato per filtrare e raggruppare i viaggi nella UI.
 */
enum class TripType {
    LOCAL,      // Spostamenti brevi vicino casa (es. gita al parco)
    DAY_TRIP,   // Escursioni di un giorno (es. visita a una città vicina)
    MULTI_DAY,  // Viaggi di più giorni (es. vacanze, weekend fuori)
    OTHER       // Categoria di fallback per valori legacy o non specificati
}

/**
 * Rappresenta un singolo percorso registrato durante il tracking GPS.
 *
 * Un viaggio (Trip) può avere più Journey, ad esempio se l'utente
 * ferma e riavvia il tracking più volte durante lo stesso viaggio.
 *
 * @property coordinates Lista ordinata dei punti GPS registrati
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
 * Un singolo punto GPS con timestamp.
 * Usato per costruire la polyline del percorso sulla mappa.
 */
data class Coordinate(
    val latitude: Double,
    val longitude: Double,
    val timestamp: Date = Date()
)

/**
 * Foto con nota opzionale, associata a un viaggio.
 * Le coordinate permettono di mostrare la foto sulla mappa nel punto esatto.
 */
data class PhotoNote(
    val id: Long = 0,
    val tripId: Long,
    val imagePath: String,      // Percorso locale del file immagine
    val note: String,           // Descrizione/didascalia della foto
    val latitude: Double? = null,
    val longitude: Double? = null,
    val timestamp: Date = Date()
)

/**
 * Nota testuale con posizione opzionale.
 * L'utente può aggiungere appunti durante il viaggio, anche senza foto.
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
 * Punto GPS dettagliato con dati dei sensori.
 * Usato internamente durante il tracking per avere più precisione.
 * Include altitudine, accuratezza GPS e velocità istantanea.
 */
data class LocationPoint(
    val id: Long = 0,
    val journeyId: Long,
    val latitude: Double,
    val longitude: Double,
    val altitude: Double? = null,   // Altitudine in metri (se disponibile)
    val accuracy: Float? = null,    // Accuratezza GPS in metri
    val speed: Float? = null,       // Velocità in m/s
    val timestamp: Long
)

/**
 * Aggregazione di tutti i dati di un viaggio per la schermata di dettaglio.
 * Contiene il viaggio base più tutti gli elementi collegati.
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

// Type alias per retrocompatibilità con il codice esistente
typealias Photo = PhotoNote
typealias Point = LocationPoint

/**
 * Area geografica monitorata per le notifiche di geofencing.
 * Quando l'utente entra o esce da quest'area riceve una notifica.
 */
data class GeofenceArea(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val radiusMeters: Float
)

/**
 * Evento di transizione geofence (ingresso o uscita da un'area).
 * Questi eventi vengono salvati per mostrare la cronologia nella mappa.
 */
data class GeofenceEvent(
    val id: Long = 0,
    val geofenceId: String,
    val transition: String,     // "ENTER" o "EXIT"
    val timestamp: Long
)

// ==================== MODELLI PER LE STATISTICHE ====================

/**
 * Statistiche aggregate per mese, usate nella schermata statistiche.
 */
data class MonthlyStat(
    val month: Int,             // Mese (1-12)
    val tripCount: Int,
    val totalDistance: Float,   // In km
    val totalDuration: Long     // In millisecondi
)

/**
 * Statistiche raggruppate per tipo di viaggio.
 * Permette di vedere quanti km ha fatto l'utente per categoria.
 */
data class TripTypeStat(
    val tripType: TripType,
    val totalDistance: Float,
    val totalDuration: Long,
    val tripCount: Int
)
