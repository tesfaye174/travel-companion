package com.travelcompanion.domain.model

import java.util.Date

/**
 * Rappresenta un viaggio completo con tutti i dati associati.
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

enum class TripType {
    LOCAL,      // gite locali
    DAY_TRIP,   // escursioni di un giorno
    MULTI_DAY,  // viaggi più lunghi
    OTHER       // valori sconosciuti
}

/**
 * Un segmento di percorso registrato durante il tracciamento.
 */
data class Journey(
    val id: Long = 0,
    val tripId: Long,
    val startTime: Date,
    val endTime: Date?,
    val distance: Float = 0f,
    val coordinates: List<Coordinate> = emptyList()
)

// Singolo punto GPS
data class Coordinate(
    val latitude: Double,
    val longitude: Double,
    val timestamp: Date = Date()
)

// Foto con ubicazione opzionale
data class PhotoNote(
    val id: Long = 0,
    val tripId: Long,
    val imagePath: String,
    val note: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val timestamp: Date = Date()
)

// Nota testuale associata al viaggio
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

// Punto GPS dettagliato con dati dai sensori
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

// Dati aggregati del viaggio per la schermata di dettaglio
data class TripDetails(
    val trip: Trip,
    val journeys: List<Journey> = emptyList(),
    val photos: List<PhotoNote> = emptyList(),
    val notes: List<Note> = emptyList(),
    val totalDistance: Double = 0.0,
    val totalDuration: Long = 0,
    val locationPoints: List<LocationPoint> = emptyList()
)

// Alias per chiarezza quando PhotoNote rappresenta una foto
typealias Photo = PhotoNote

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

// Statistiche mensili usate dal repository
data class MonthlyStat(
    val month: Int,
    val tripCount: Int,
    val totalDistance: Float,
    val totalDuration: Long
)

// Statistiche aggregate per tipo di viaggio usate nell'app
data class TripTypeStat(
    val tripType: TripType,
    val totalDistance: Float,
    val totalDuration: Long,
    val tripCount: Int
)
