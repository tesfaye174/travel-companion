package com.example.travelcompanion.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "location_points",
    foreignKeys = [ForeignKey(
        entity = Journey::class,
        parentColumns = ["id"],
        childColumns = ["journeyId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class LocationPoint(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val journeyId: Long,
    val latitude: Double,
    val longitude: Double,
    val altitude: Double? = null,
    val accuracy: Float? = null,
    val speed: Float? = null,
    val timestamp: Long
)
