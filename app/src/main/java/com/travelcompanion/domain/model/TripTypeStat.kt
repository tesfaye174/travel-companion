package com.travelcompanion.domain.model

/**
 * Represents statistics for a specific trip type.
 */
data class TripTypeStat(
    val tripType: TripType,
    val totalDistance: Float,
    val totalDuration: Long,
    val tripCount: Int
)

data class TripTypeStatEntity(
    @ColumnInfo(name = "tripType")
    val tripType: String,
    @ColumnInfo(name = "totalDistance")
    val totalDistance: Float,
    @ColumnInfo(name = "totalDuration")
    val totalDuration: Long,
    @ColumnInfo(name = "tripCount")
    val tripCount: Int
)