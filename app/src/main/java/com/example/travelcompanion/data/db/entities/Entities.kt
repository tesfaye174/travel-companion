package com.example.travelcompanion.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val destination: String,
    val startDate: Long, // Timestamp
    val endDate: Long?,  // Timestamp
    val type: String,    // "Local", "Day", "Multi-day"
    val isCompleted: Boolean = false,
    val totalDistance: Double = 0.0,
    val totalTime: Long = 0 // Seconds
)

@Entity(tableName = "journeys")
data class JourneyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tripId: Long,
    val startTime: Long,
    val endTime: Long?,
    val distance: Double = 0.0,
    val time: Long = 0
)

@Entity(tableName = "points")
data class PointEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val journeyId: Long,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long,
    val speed: Float,
    val accuracy: Float
)

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tripId: Long,
    val journeyId: Long?,
    val pointId: Long?,
    val content: String,
    val timestamp: Long
)

@Entity(tableName = "photos")
data class PhotoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tripId: Long,
    val journeyId: Long?,
    val pointId: Long?,
    val filePath: String,
    val timestamp: Long
)
