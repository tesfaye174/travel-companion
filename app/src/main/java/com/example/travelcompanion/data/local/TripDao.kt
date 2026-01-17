package com.example.travelcompanion.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.travelcompanion.domain.model.Trip
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {
    @Query("SELECT * FROM trips ORDER BY startDate DESC")
    fun getAllTrips(): Flow<List<Trip>>

    @Query("SELECT * FROM trips WHERE startDate >= :currentTime ORDER BY startDate ASC")
    fun getUpcomingTrips(currentTime: Long): Flow<List<Trip>>

    @Query("SELECT * FROM trips WHERE endDate < :currentTime ORDER BY startDate DESC")
    fun getPastTrips(currentTime: Long): Flow<List<Trip>>

    @Query("SELECT * FROM trips WHERE id = :tripId")
    fun getTripById(tripId: Long): Flow<Trip?>

    @Query("SELECT * FROM trips WHERE isActive = 1 LIMIT 1")
    fun getActiveTrip(): Flow<Trip?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: Trip): Long

    @Update
    suspend fun updateTrip(trip: Trip)

    @Delete
    suspend fun deleteTrip(trip: Trip)

    @Query("UPDATE trips SET isActive = 0")
    suspend fun deactivateAllTrips()

    @Query("UPDATE trips SET isActive = 1 WHERE id = :tripId")
    suspend fun activateTrip(tripId: Long)
}
