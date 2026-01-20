package com.travelcompanion.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "geofence_events")
data class GeofenceEventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "geofence_id")
    val geofenceId: String,

    @ColumnInfo(name = "transition")
    val transition: String,

    @ColumnInfo(name = "timestamp")
    val timestamp: Long
)
