package com.travelcompanion.data.db.dao

import androidx.room.*
import com.travelcompanion.data.db.entities.TripEntity
import com.travelcompanion.domain.model.TripType
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: TripEntity): Long

    @Update
    suspend fun updateTrip(trip: TripEntity)

    @Delete
    suspend fun deleteTrip(trip: TripEntity)

    @Query("DELETE FROM trips")
    suspend fun deleteAllTrips()

    @Query("SELECT * FROM trips WHERE id = :id")
    fun getTripByIdFlow(id: Long): Flow<TripEntity?>

    @Query("SELECT * FROM trips ORDER BY start_date DESC")
    fun getAllTripsFlow(): Flow<List<TripEntity>>

    @Query("SELECT * FROM trips WHERE trip_type = :type ORDER BY start_date DESC")
    fun getTripsByTypeFlow(type: TripType): Flow<List<TripEntity>>

    @Transaction
    @Query("SELECT * FROM trips WHERE start_date BETWEEN :startDate AND :endDate")
    fun getTripsBetweenDatesFlow(startDate: Long, endDate: Long): Flow<List<TripEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrips(trips: List<TripEntity>): List<Long>

    @Query("SELECT SUM(total_distance) FROM trips")
    suspend fun getTotalDistance(): Float?

    @Query("SELECT SUM(total_duration) FROM trips")
    suspend fun getTotalDuration(): Long?

    @Query("SELECT COUNT(*) FROM trips")
    suspend fun getTripCount(): Int
}
