package com.travelcompanion.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.travelcompanion.data.db.entities.GeofenceAreaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GeofenceAreaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(area: GeofenceAreaEntity)

    @Query("SELECT * FROM geofence_areas")
    fun getAll(): Flow<List<GeofenceAreaEntity>>
}
