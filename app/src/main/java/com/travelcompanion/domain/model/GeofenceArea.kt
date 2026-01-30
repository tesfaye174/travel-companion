package com.travelcompanion.domain.model

/**
 * Represents a geofence area.
 */
data class GeofenceArea(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val radiusMeters: Float
)