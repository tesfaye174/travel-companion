package com.travelcompanion.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.travelcompanion.utils.Converters

enum class TripType { LOCAL, DAY_TRIP, MULTI_DAY }

@TypeConverters(Converters::class)
@Entity(tableName = "trips")
data class Trip(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val destination: String,
    val type: TripType,
    val startDate: Long,
    val endDate: Long,
    val isActive: Boolean = false
)
