package com.example.travelcompanion.data.repository

import com.example.travelcompanion.domain.repository.ITripRepository
import javax.inject.Inject

class TripRepositoryImpl @Inject constructor(
    private val tripRepository: TripRepository
) : ITripRepository {

    override suspend fun insertTrip(trip: com.example.travelcompanion.domain.model.Trip): Long {
        return tripRepository.insertTrip(trip)
    }

    override suspend fun updateTrip(trip: com.example.travelcompanion.domain.model.Trip) {
        tripRepository.updateTrip(trip)
    }

    override suspend fun deleteTrip(trip: com.example.travelcompanion.domain.model.Trip) {
        tripRepository.deleteTrip(trip)
    }

    override suspend fun getTripById(id: Long): com.example.travelcompanion.domain.model.Trip? {
        return tripRepository.getTripById(id)
    }

    override fun getAllTrips(): kotlinx.coroutines.flow.Flow<List<com.example.travelcompanion.domain.model.Trip>> {
        return tripRepository.getAllTrips()
    }

    override fun getTripsByType(type: com.example.travelcompanion.domain.model.TripType): kotlinx.coroutines.flow.Flow<List<com.example.travelcompanion.domain.model.Trip>> {
        return tripRepository.getTripsByType(type)
    }

    override fun getTripsBetweenDates(start: java.util.Date, end: java.util.Date): kotlinx.coroutines.flow.Flow<List<com.example.travelcompanion.domain.model.Trip>> {
        return tripRepository.getTripsBetweenDates(start, end)
    }

    override suspend fun insertJourney(journey: com.example.travelcompanion.domain.model.Journey): Long {
        return tripRepository.insertJourney(journey)
    }

    override suspend fun updateJourney(journey: com.example.travelcompanion.domain.model.Journey) {
        tripRepository.updateJourney(journey)
    }

    override suspend fun deleteJourney(journey: com.example.travelcompanion.domain.model.Journey) {
        tripRepository.deleteJourney(journey)
    }

    override fun getJourneysByTripId(tripId: Long): kotlinx.coroutines.flow.Flow<List<com.example.travelcompanion.domain.model.Journey>> {
        return tripRepository.getJourneysByTripId(tripId)
    }

    override suspend fun insertPhotoNote(photoNote: com.example.travelcompanion.domain.model.PhotoNote): Long {
        return tripRepository.insertPhotoNote(photoNote)
    }

    override suspend fun updatePhotoNote(photoNote: com.example.travelcompanion.domain.model.PhotoNote) {
        tripRepository.updatePhotoNote(photoNote)
    }

    override suspend fun deletePhotoNote(photoNote: com.example.travelcompanion.domain.model.PhotoNote) {
        tripRepository.deletePhotoNote(photoNote)
    }

    override fun getPhotoNotesByTripId(tripId: Long): kotlinx.coroutines.flow.Flow<List<com.example.travelcompanion.domain.model.PhotoNote>> {
        return tripRepository.getPhotoNotesByTripId(tripId)
    }

    override suspend fun getTotalDistance(): Float {
        return tripRepository.getTotalDistance()
    }

    override suspend fun getTotalDuration(): Long {
        return tripRepository.getTotalDuration()
    }

    override suspend fun getTripCount(): Int {
        return tripRepository.getTripCount()
    }

    override suspend fun getMonthlyStats(): List<ITripRepository.MonthlyStat> {
        return tripRepository.getMonthlyStats()
    }

    override suspend fun getTripTypeStats(): List<ITripRepository.TripTypeStat> {
        return tripRepository.getTripTypeStats()
    }
}