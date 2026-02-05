package com.travelcompanion.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pins")
data class PinEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val latitude: Double,
    val longitude: Double,
    val name: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
