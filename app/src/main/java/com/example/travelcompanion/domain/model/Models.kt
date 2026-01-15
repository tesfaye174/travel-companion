package com.example.travelcompanion.domain.model

data class Trip(
    val id: Long = 0,
    val destination: String,
    val startDate: Long,
    val endDate: Long?,
    val type: TripType,
    val isCompleted: Boolean = false,
    val totalDistance: Double = 0.0,
    val totalTime: Long = 0
)

enum class TripType {
    LOCAL, DAY, MULTI_DAY
}

data class Journey(
    val id: Long = 0,
    val tripId: Long,
    val startTime: Long,
    val endTime: Long?,
    val distance: Double = 0.0,
    val time: Long = 0
)

data class Point(
    val id: Long = 0,
    val journeyId: Long,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long,
    val speed: Float,
    val accuracy: Float
)

data class Photo(
    val id: Long = 0,
    val tripId: Long,
    val journeyId: Long?,
    val pointId: Long?,
    val filePath: String,
    val timestamp: Long
)
