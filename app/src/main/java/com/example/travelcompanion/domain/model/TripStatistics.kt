package com.example.travelcompanion.domain.model

data class TripStatistics(
    val totalTrips: Int,
    val totalDistance: Double, // in km
    val totalDuration: Long, // in milliseconds
    val tripsPerMonth: Map<String, Int>,
    val averageSpeed: Double,
    val photoCount: Int
)

