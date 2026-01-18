package com.example.travelcompanion.data.db.dao

import androidx.room.*
import com.example.travelcompanion.data.db.entities.JourneyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JourneyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJourney(journey: JourneyEntity): Long

    @Update
    suspend fun updateJourney(journey: JourneyEntity)

    @Delete
    suspend fun deleteJourney(journey: JourneyEntity)

    @Query("SELECT * FROM journeys WHERE trip_id = :tripId ORDER BY start_time DESC")
    fun getJourneysByTripId(tripId: Long): Flow<List<JourneyEntity>>

    @Query("SELECT * FROM journeys WHERE id = :id")
    suspend fun getJourneyById(id: Long): JourneyEntity?

    @Query("DELETE FROM journeys WHERE trip_id = :tripId")
    suspend fun deleteJourneysByTripId(tripId: Long)
}