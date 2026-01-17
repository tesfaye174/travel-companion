package com.example.travelcompanion.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.travelcompanion.domain.model.Journey
import kotlinx.coroutines.flow.Flow

@Dao
interface JourneyDao {
    @Query("SELECT * FROM journeys WHERE tripId = :tripId ORDER BY startTime DESC")
    fun getJourneysForTrip(tripId: Long): Flow<List<Journey>>

    @Query("SELECT * FROM journeys WHERE isActive = 1 LIMIT 1")
    fun getActiveJourney(): Flow<Journey?>

    @Query("SELECT * FROM journeys WHERE id = :journeyId")
    fun getJourneyById(journeyId: Long): Flow<Journey?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJourney(journey: Journey): Long

    @Update
    suspend fun updateJourney(journey: Journey)

    @Delete
    suspend fun deleteJourney(journey: Journey)

    @Query("UPDATE journeys SET isActive = 0, endTime = :endTime WHERE id = :journeyId")
    suspend fun stopJourney(journeyId: Long, endTime: Long)
}