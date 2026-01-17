package com.example.travelcompanion.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "journeys",
    foreignKeys = [ForeignKey(
        entity = Trip::class,
        parentColumns = ["id"],
        childColumns = ["tripId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Journey(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tripId: Long,
    val startTime: Long,
    val endTime: Long? = null,
    val isActive: Boolean = true
)