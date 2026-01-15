package com.example.travelcompanion.domain.repository

import com.example.travelcompanion.domain.model.*
import kotlinx.coroutines.flow.Flow

interface TravelRepository {
    fun getAllTrips(): Flow<List<Trip>>
    suspend fun getTripById(tripId: Long): Trip?
    suspend fun insertTrip(trip: Trip): Long
    suspend fun updateTrip(trip: Trip)
    suspend fun deleteTrip(trip: Trip)

    fun getJourneysByTrip(tripId: Long): Flow<List<Journey>>
    suspend fun insertJourney(journey: Journey): Long
    
    fun getPointsByJourney(journeyId: Long): Flow<List<Point>>
    suspend fun insertPoint(point: Point)

    suspend fun insertPhoto(photo: Photo)
    fun getPhotosByTrip(tripId: Long): Flow<List<Photo>>
}
