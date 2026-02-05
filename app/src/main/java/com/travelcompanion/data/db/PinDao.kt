package com.travelcompanion.data.db

import androidx.room.*

@Dao
interface PinDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPin(pin: PinEntity): Long

    @Query("SELECT * FROM pins")
    suspend fun getAllPins(): List<PinEntity>

    @Delete
    suspend fun deletePin(pin: PinEntity)

    @Query("DELETE FROM pins")
    suspend fun deleteAllPins()
}
