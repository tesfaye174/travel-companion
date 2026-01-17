package com.example.travelcompanion.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trips")
data class Trip(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val destination: String,
    val tripType: TripType,
    val startDate: Long,
    val endDate: Long,
    val coverImageUrl: String? = null,
    val notes: String? = null,
    val isActive: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

enum class TripType {
    CITY_TRIP,
    MULTI_DAY,
    HIKING,
    BEACH,
    BUSINESS,
    OTHER
}