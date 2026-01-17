package com.example.travelcompanion.domain.model

data class TripDetails(
    val trip: Trip,
    val journeys: List<Journey>,
    val photos: List<Photo>,
    val notes: List<Note>,
    val totalDistance: Double,
    val totalDuration: Long,
    val locationPoints: List<LocationPoint>
)