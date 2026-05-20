package com.travelcompanion.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.travelcompanion.domain.model.TripType

/**
 * Trip entity representing a travel trip in the database.
 * Includes indices for optimized query performance.
 */
@Entity(
    tableName = "trips",
    indices = [
        Index(value = ["start_date"], name = "idx_trip_start_date"),
        Index(value = ["trip_type"], name = "idx_trip_type"),
        Index(value = ["is_tracking"], name = "idx_trip_is_tracking"),
        Index(value = ["destination"], name = "idx_trip_destination")
    ]
)
data class TripEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "destination")
    val destination: String,

    @ColumnInfo(name = "trip_type")
    val tripType: TripType,

    @ColumnInfo(name = "start_date")
    val startDate: Long, // timestamp in milliseconds

    @ColumnInfo(name = "end_date")
    val endDate: Long, // timestamp in milliseconds

    @ColumnInfo(name = "total_distance")
    val totalDistance: Float = 0f, // in meters

    @ColumnInfo(name = "total_duration")
    val totalDuration: Long = 0, // in milliseconds

    @ColumnInfo(name = "photo_count")
    val photoCount: Int = 0,

    @ColumnInfo(name = "notes")
    val notes: String = "",

    @ColumnInfo(name = "is_tracking")
    val isTracking: Boolean = false,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)

