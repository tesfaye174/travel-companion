package com.travelcompanion.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.travelcompanion.data.database.converters.DateConverter
import java.util.Date
import java.util.UUID

@Entity(tableName = "trips")
@TypeConverters(DateConverter::class)
data class Trip(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val destination: String,
    val tripType: TripType,
    val startDate: Date,
    val endDate: Date,
    val isActive: Boolean = false,
    val distance: Float = 0f,
    val duration: Long = 0,
    val notes: String = "",
    val createdAt: Date = Date()
) {
    enum class TripType {
        LOCAL,
        DAY_TRIP,
        MULTI_DAY
    }

    fun getTypeDisplayName(): String {
        return when (tripType) {
            TripType.LOCAL -> "Locale"
            TripType.DAY_TRIP -> "Giornaliero"
            TripType.MULTI_DAY -> "Multi-giorno"
        }
    }
}