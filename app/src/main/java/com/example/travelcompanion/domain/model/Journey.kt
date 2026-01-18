package com.example.travelcompanion.domain.model

import java.util.Date

data class Journey(
    val id: Long = 0,
    val tripId: Long,
    val startTime: Date,
    val endTime: Date,
    val distance: Float, // in km
    val coordinates: List<Coordinate>
)

data class Coordinate(
    val latitude: Double,
    val longitude: Double,
    val timestamp: Date = Date()
)