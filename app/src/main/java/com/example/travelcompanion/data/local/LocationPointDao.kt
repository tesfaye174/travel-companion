package com.example.travelcompanion.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.travelcompanion.domain.model.LocationPoint

@Dao
interface LocationPointDao {
    @Query("SELECT * FROM location_points WHERE journeyId = :journeyId ORDER BY timestamp ASC")
    fun getPointsForJourney(journeyId: Long): LiveData<List<LocationPoint>>

    @Query("SELECT * FROM location_points WHERE journeyId = :journeyId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastPointForJourney(journeyId: Long): LocationPoint?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPoint(point: LocationPoint): Long

    @Query("DELETE FROM location_points WHERE journeyId = :journeyId")
    suspend fun deletePointsForJourney(journeyId: Long)

    @Query("SELECT COUNT(*) FROM location_points WHERE journeyId = :journeyId")
    suspend fun getPointCount(journeyId: Long): Int
}