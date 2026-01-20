package com.travelcompanion.domain.repository

import com.travelcompanion.domain.model.Trip
import com.travelcompanion.domain.model.TripType
import com.travelcompanion.domain.model.Journey
import com.travelcompanion.domain.model.PhotoNote
import com.travelcompanion.domain.model.Note
import com.travelcompanion.domain.model.GeofenceArea
import com.travelcompanion.domain.model.GeofenceEvent
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
    fun getAllJourneys(): Flow<List<Journey>>

    // PhotoNote operations
    suspend fun insertPhotoNote(photoNote: PhotoNote): Long
    suspend fun updatePhotoNote(photoNote: PhotoNote)
    suspend fun deletePhotoNote(photoNote: PhotoNote)
    fun getPhotoNotesByTripId(tripId: Long): Flow<List<PhotoNote>>

    // Note operations (text notes attached to moments/locations)
    suspend fun insertNote(note: Note): Long
    suspend fun updateNote(note: Note)
    suspend fun deleteNote(note: Note)
    fun getNotesByTripId(tripId: Long): Flow<List<Note>>

    // Geofencing (areas + events)
    suspend fun upsertGeofenceArea(id: String, name: String, lat: Double, lon: Double, radiusMeters: Float)
    fun getGeofenceAreas(): Flow<List<GeofenceArea>>
    fun getGeofenceEvents(): Flow<List<GeofenceEvent>>

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

