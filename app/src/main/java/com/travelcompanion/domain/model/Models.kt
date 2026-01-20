package com.travelcompanion.domain.model

import java.util.Date

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
    LOCAL,
    DAY_TRIP,
    MULTI_DAY
}

data class Journey(
    val id: Long = 0,
    val tripId: Long,
    val startTime: Date,
    val endTime: Date?,
    val distance: Float = 0f,
    val coordinates: List<Coordinate> = emptyList()
)

data class Coordinate(
    val latitude: Double,
    val longitude: Double,
    val timestamp: Date = Date()
)

data class PhotoNote(
    val id: Long = 0,
    val tripId: Long,
    val imagePath: String,
    val note: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val timestamp: Date = Date()
)

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

