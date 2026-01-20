package com.travelcompanion.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.travelcompanion.data.db.entities.GeofenceEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GeofenceEventDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(event: GeofenceEventEntity): Long

    @Query("SELECT * FROM geofence_events ORDER BY timestamp DESC")
    fun getAll(): Flow<List<GeofenceEventEntity>>
}
