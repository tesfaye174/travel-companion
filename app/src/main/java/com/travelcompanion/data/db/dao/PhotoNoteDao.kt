package com.travelcompanion.data.db.dao

import androidx.room.*
import com.travelcompanion.data.db.entities.PhotoNoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoNoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotoNote(photoNote: PhotoNoteEntity): Long

    @Update
    suspend fun updatePhotoNote(photoNote: PhotoNoteEntity)

    @Delete
    suspend fun deletePhotoNote(photoNote: PhotoNoteEntity)

    @Query("SELECT * FROM photo_notes WHERE trip_id = :tripId ORDER BY timestamp DESC")
    fun getPhotoNotesByTripId(tripId: Long): Flow<List<PhotoNoteEntity>>

    @Query("SELECT * FROM photo_notes WHERE id = :id")
    suspend fun getPhotoNoteById(id: Long): PhotoNoteEntity?

    @Query("DELETE FROM photo_notes WHERE trip_id = :tripId")
    suspend fun deletePhotoNotesByTripId(tripId: Long)

    @Query("SELECT COUNT(*) FROM photo_notes WHERE trip_id = :tripId")
    suspend fun getPhotoCountByTripId(tripId: Long): Int
}

