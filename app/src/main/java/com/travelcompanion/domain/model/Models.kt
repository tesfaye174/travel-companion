package com.travelcompanion.domain.model

import java.util.Date

/**
 * Modelli del dominio dell'app.
 * Sono separati dalle entity di Room cosi il resto dell'app
 * non dipende dal database. La conversione la fa il repository.
 */

// rappresenta un viaggio
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
    val isTracking: Boolean = false,
    val destinationLatitude: Double? = null,
    val destinationLongitude: Double? = null
)

// tipi di viaggio per i filtri
enum class TripType {
    LOCAL,
    DAY_TRIP,
    MULTI_DAY,
    OTHER
}

// percorso registrato col gps durante un viaggio
data class Journey(
    val id: Long = 0,
    val tripId: Long,
    val startTime: Date,
    val endTime: Date?,
    val distance: Float = 0f,
    val coordinates: List<Coordinate> = emptyList()
)

// punto gps singolo
data class Coordinate(
    val latitude: Double,
    val longitude: Double,
    val timestamp: Date = Date()
)

// foto con nota associata al viaggio
data class PhotoNote(
    val id: Long = 0,
    val tripId: Long,
    val imagePath: String,
    val note: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val timestamp: Date = Date()
)

// nota testuale
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

// punto gps con dati extra dei sensori
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

// aggregazione di tutti i dati di un viaggio
data class TripDetails(
    val trip: Trip,
    val journeys: List<Journey> = emptyList(),
    val photos: List<PhotoNote> = emptyList(),
    val notes: List<Note> = emptyList(),
    val totalDistance: Double = 0.0,
    val totalDuration: Long = 0,
    val locationPoints: List<LocationPoint> = emptyList()
)

// alias per retrocompatibilita
typealias Photo = PhotoNote
typealias Point = LocationPoint

// area geografica per le notifiche geofence
data class GeofenceArea(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val radiusMeters: Float
)

// evento quando entri/esci da un'area geofence
data class GeofenceEvent(
    val id: Long = 0,
    val geofenceId: String,
    val transition: String,
    val timestamp: Long
)

// --- modelli per le statistiche ---

data class MonthlyStat(
    val month: Int,
    val tripCount: Int,
    val totalDistance: Float,
    val totalDuration: Long
)

data class TripTypeStat(
    val tripType: TripType,
    val totalDistance: Float,
    val totalDuration: Long,
    val tripCount: Int
)
