package com.example.travelcompanion.domain.repository

import com.example.travelcompanion.domain.model.Trip
import com.example.travelcompanion.domain.model.TripType
import com.example.travelcompanion.domain.model.Journey
import com.example.travelcompanion.domain.model.PhotoNote
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface ITripRepository {
    // Trip operations
    suspend fun insertTrip(trip: Trip): Long
    suspend fun updateTrip(trip: Trip)
    suspend fun deleteTrip(trip: Trip)
    suspend fun getTripById(id: Long): Trip?
    fun getAllTrips(): Flow<List<Trip>>
    fun getTripsByType(type: TripType): Flow<List<Trip>>
    fun getTripsBetweenDates(start: Date, end: Date): Flow<List<Trip>>

    // Journey operations
    suspend fun insertJourney(journey: Journey): Long
    suspend fun updateJourney(journey: Journey)
    suspend fun deleteJourney(journey: Journey)
    fun getJourneysByTripId(tripId: Long): Flow<List<Journey>>

    // PhotoNote operations
    suspend fun insertPhotoNote(photoNote: PhotoNote): Long
    suspend fun updatePhotoNote(photoNote: PhotoNote)
    suspend fun deletePhotoNote(photoNote: PhotoNote)
    fun getPhotoNotesByTripId(tripId: Long): Flow<List<PhotoNote>>

    // Statistics
    suspend fun getTotalDistance(): Float
    suspend fun getTotalDuration(): Long
    suspend fun getTripCount(): Int
    suspend fun getMonthlyStats(): List<MonthlyStat>
    suspend fun getTripTypeStats(): List<TripTypeStat>

    data class MonthlyStat(
        val month: String,
        val tripCount: Int,
        val totalDistance: Float,
        val totalDuration: Long
    )

    data class TripTypeStat(
        val type: TripType,
        val count: Int,
        val percentage: Float
    )
}