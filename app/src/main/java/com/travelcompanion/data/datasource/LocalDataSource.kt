package com.travelcompanion.data.datasource

import com.travelcompanion.data.db.AppDatabase
import com.travelcompanion.data.db.entities.JourneyEntity
import com.travelcompanion.data.db.entities.PhotoNoteEntity
import com.travelcompanion.data.db.entities.TripEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val database: AppDatabase
) {

    // Trip operations
    suspend fun insertTrip(trip: TripEntity): Long {
        return database.tripDao().insertTrip(trip)
    }

    suspend fun updateTrip(trip: TripEntity) {
        database.tripDao().updateTrip(trip)
    }

    suspend fun deleteTrip(trip: TripEntity) {
        database.tripDao().deleteTrip(trip)
    }

    fun getAllTrips(): Flow<List<TripEntity>> {
        return database.tripDao().getAllTripsFlow()
    }

    fun getTripsByType(type: String): Flow<List<TripEntity>> {
        return database.tripDao().getTripsByTypeFlow(com.travelcompanion.domain.model.TripType.valueOf(type))
    }

    // Journey operations
    suspend fun insertJourney(journey: JourneyEntity): Long {
        return database.journeyDao().insertJourney(journey)
    }

    fun getJourneysByTripId(tripId: Long): Flow<List<JourneyEntity>> {
        return database.journeyDao().getJourneysByTripId(tripId)
    }

    // PhotoNote operations
    suspend fun insertPhotoNote(photoNote: PhotoNoteEntity): Long {
        return database.photoNoteDao().insertPhotoNote(photoNote)
    }

    fun getPhotoNotesByTripId(tripId: Long): Flow<List<PhotoNoteEntity>> {
        return database.photoNoteDao().getPhotoNotesByTripId(tripId)
    }
}

