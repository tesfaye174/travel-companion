package com.example.travelcompanion.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.travelcompanion.domain.model.Photo
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {
    @Query("SELECT * FROM photos WHERE tripId = :tripId ORDER BY timestamp DESC")
    fun getPhotosForTrip(tripId: Long): Flow<List<Photo>>

    @Query("SELECT * FROM photos WHERE journeyId = :journeyId ORDER BY timestamp DESC")
    fun getPhotosForJourney(journeyId: Long): Flow<List<Photo>>

    @Query("SELECT COUNT(*) FROM photos WHERE tripId = :tripId")
    suspend fun getPhotoCountForTrip(tripId: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: Photo): Long

    @Delete
    suspend fun deletePhoto(photo: Photo)

    @Query("DELETE FROM photos WHERE tripId = :tripId")
    suspend fun deletePhotosForTrip(tripId: Long)
}
