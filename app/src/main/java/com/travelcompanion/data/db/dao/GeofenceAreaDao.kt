package com.travelcompanion.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.travelcompanion.data.db.entities.GeofenceAreaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GeofenceAreaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(area: GeofenceAreaEntity)

    @Query("SELECT * FROM geofence_areas")
    fun getAll(): Flow<List<GeofenceAreaEntity>>

    @Query("SELECT * FROM geofence_areas WHERE id = :areaId")
    suspend fun getById(areaId: String): GeofenceAreaEntity?

    @Delete
    suspend fun delete(area: GeofenceAreaEntity)

    @Query("DELETE FROM geofence_areas WHERE id = :areaId")
    suspend fun deleteById(areaId: String)

    @Update
    suspend fun update(area: GeofenceAreaEntity)
}
