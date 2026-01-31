package com.travelcompanion.data.local

import com.travelcompanion.data.local.entity.LocationPoint
import com.travelcompanion.data.local.entity.Trip
import kotlinx.coroutines.flow.Flow

@androidx.room.Dao
interface TripDao {
    @androidx.room.Query("SELECT * FROM trips ORDER BY startDate DESC")
    fun getAllTrips(): Flow<List<Trip>>

    @androidx.room.Query("SELECT * FROM trips WHERE isActive = 1 LIMIT 1")
    suspend fun getActiveTrip(): Trip?

    @androidx.room.Insert
    suspend fun insertTrip(trip: Trip): Long

    @androidx.room.Update
    suspend fun updateTrip(trip: Trip)

    @androidx.room.Query("SELECT * FROM locations WHERE tripId = :tripId")
    fun getLocationsForTrip(tripId: Long): Flow<List<LocationPoint>>

    @androidx.room.Insert
    suspend fun insertLocation(location: LocationPoint)
}
