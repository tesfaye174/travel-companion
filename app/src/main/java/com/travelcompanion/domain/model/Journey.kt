package com.travelcompanion.domain.model

import java.util.Date

/**
 * Represents a journey segment within a trip.
 */
data class Journey(
    val id: Long = 0,
    val tripId: Long,
    val startTime: Date,
    val endTime: Date,
    val distance: Float,
    val path: List<LocationPoint>
)

/**
 * Represents a location point with latitude and longitude.
 */
data class LocationPoint(
    val latitude: Double,
    val longitude: Double
)