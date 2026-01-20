package com.travelcompanion.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.travelcompanion.domain.model.TripType

@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "destination")
    val destination: String,

    @ColumnInfo(name = "trip_type")
    val tripType: TripType,

    @ColumnInfo(name = "start_date")
    val startDate: Long, // timestamp

    @ColumnInfo(name = "end_date")
    val endDate: Long,

    @ColumnInfo(name = "total_distance")
    val totalDistance: Float = 0f,

    @ColumnInfo(name = "total_duration")
    val totalDuration: Long = 0,

    @ColumnInfo(name = "photo_count")
    val photoCount: Int = 0,

    @ColumnInfo(name = "notes")
    val notes: String = "",

    @ColumnInfo(name = "is_tracking")
    val isTracking: Boolean = false
)

