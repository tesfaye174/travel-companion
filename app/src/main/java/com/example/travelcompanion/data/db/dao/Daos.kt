package com.example.travelcompanion.data.db.dao

import androidx.room.*
import com.example.travelcompanion.data.db.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {
    @Query("SELECT * FROM trips ORDER BY startDate DESC")
    fun getAllTrips(): Flow<List<TripEntity>>

    @Insert
    suspend fun insertTrip(trip: TripEntity): Long

    @Update
    suspend fun updateTrip(trip: TripEntity)

    @Delete
    suspend fun deleteTrip(trip: TripEntity)

    @Query("SELECT * FROM trips WHERE id = :tripId")
    suspend fun getTripById(tripId: Long): TripEntity?
}

@Dao
interface JourneyDao {
    @Query("SELECT * FROM journeys WHERE tripId = :tripId")
    fun getJourneysByTrip(tripId: Long): Flow<List<JourneyEntity>>

    @Insert
    suspend fun insertJourney(journey: JourneyEntity): Long

    @Update
    suspend fun updateJourney(journey: JourneyEntity)

    @Query("SELECT * FROM points WHERE journeyId = :journeyId ORDER BY timestamp ASC")
    fun getPointsByJourney(journeyId: Long): Flow<List<PointEntity>>

    @Insert
    suspend fun insertPoint(point: PointEntity)

    @Query("SELECT * FROM notes WHERE tripId = :tripId")
    fun getNotesByTrip(tripId: Long): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE journeyId = :journeyId")
    fun getNotesByJourney(journeyId: Long): Flow<List<NoteEntity>>

    @Insert
    suspend fun insertNote(note: NoteEntity)

    @Query("SELECT * FROM photos WHERE tripId = :tripId")
    fun getPhotosByTrip(tripId: Long): Flow<List<PhotoEntity>>

    @Insert
    suspend fun insertPhoto(photo: PhotoEntity)
}
