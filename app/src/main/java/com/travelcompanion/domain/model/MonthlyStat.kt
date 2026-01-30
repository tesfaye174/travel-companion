package com.travelcompanion.domain.model

/**
 * Represents monthly statistics for trips.
 */
data class MonthlyStat(
    val month: String,
    val totalDistance: Float,
    val totalDuration: Long,
    val tripCount: Int
)