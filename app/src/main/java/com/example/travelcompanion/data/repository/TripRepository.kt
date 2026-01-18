package com.example.travelcompanion.data.repository

import com.example.travelcompanion.data.db.AppDatabase
import com.example.travelcompanion.data.db.dao.JourneyDao
import com.example.travelcompanion.data.db.dao.PhotoNoteDao
import com.example.travelcompanion.data.db.dao.TripDao
import com.example.travelcompanion.data.db.entities.JourneyEntity
import com.example.travelcompanion.data.db.entities.PhotoNoteEntity
import com.example.travelcompanion.data.db.entities.TripEntity
import com.example.travelcompanion.domain.model.*
import com.example.travelcompanion.domain.repository.ITripRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject

class TripRepository @Inject constructor(
    private val database: AppDatabase
) : ITripRepository {

    private val tripDao: TripDao = database.tripDao()
    private val journeyDao: JourneyDao = database.journeyDao()
    private val photoNoteDao: PhotoNoteDao = database.photoNoteDao()

    override suspend fun insertTrip(trip: Trip): Long {
        val entity = trip.toEntity()
        return tripDao.insertTrip(entity)
    }

    override suspend fun updateTrip(trip: Trip) {
        tripDao.updateTrip(trip.toEntity())
    }

    override suspend fun deleteTrip(trip: Trip) {
        tripDao.deleteTrip(trip.toEntity())
    }

    override suspend fun getTripById(id: Long): Trip? {
        return tripDao.getTripById(id)?.toDomain()
    }

    override fun getAllTrips(): Flow<List<Trip>> {
        return tripDao.getAllTrips().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getTripsByType(type: TripType): Flow<List<Trip>> {
        return tripDao.getTripsByType(type).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getTripsBetweenDates(start: Date, end: Date): Flow<List<Trip>> {
        return tripDao.getTripsBetweenDates(start.time, end.time).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertJourney(journey: Journey): Long {
        return journeyDao.insertJourney(journey.toEntity())
    }

    override suspend fun updateJourney(journey: Journey) {
        journeyDao.updateJourney(journey.toEntity())
    }

    override suspend fun deleteJourney(journey: Journey) {
        journeyDao.deleteJourney(journey.toEntity())
    }

    override fun getJourneysByTripId(tripId: Long): Flow<List<Journey>> {
        return journeyDao.getJourneysByTripId(tripId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertPhotoNote(photoNote: PhotoNote): Long {
        return photoNoteDao.insertPhotoNote(photoNote.toEntity())
    }

    override suspend fun updatePhotoNote(photoNote: PhotoNote) {
        photoNoteDao.updatePhotoNote(photoNote.toEntity())
    }

    override suspend fun deletePhotoNote(photoNote: PhotoNote) {
        photoNoteDao.deletePhotoNote(photoNote.toEntity())
    }

    override fun getPhotoNotesByTripId(tripId: Long): Flow<List<PhotoNote>> {
        return photoNoteDao.getPhotoNotesByTripId(tripId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getTotalDistance(): Float {
        return tripDao.getTotalDistance() ?: 0f
    }

    override suspend fun getTotalDuration(): Long {
        return tripDao.getTotalDuration() ?: 0L
    }

    override suspend fun getTripCount(): Int {
        return tripDao.getTripCount()
    }

    override suspend fun getMonthlyStats(): List<ITripRepository.MonthlyStat> {
        return tripDao.getMonthlyStats().map { stat ->
            ITripRepository.MonthlyStat(
                month = stat.month,
                tripCount = stat.tripCount,
                totalDistance = stat.totalDistance ?: 0f,
                totalDuration = stat.totalDuration ?: 0L
            )
        }
    }

    override suspend fun getTripTypeStats(): List<ITripRepository.TripTypeStat> {
        return tripDao.getTripTypeStats().map { stat ->
            ITripRepository.TripTypeStat(
                type = stat.type,
                count = stat.count,
                percentage = stat.percentage
            )
        }
    }

    // Extension functions for conversion
    private fun Trip.toEntity(): TripEntity {
        return TripEntity(
            id = id,
            title = title,
            destination = destination,
            tripType = tripType,
            startDate = startDate.time,
            endDate = endDate.time,
            totalDistance = totalDistance,
            totalDuration = totalDuration,
            photoCount = photoCount,
            notes = notes,
            isTracking = isTracking
        )
    }

    private fun TripEntity.toDomain(): Trip {
        return Trip(
            id = id,
            title = title,
            destination = destination,
            tripType = tripType,
            startDate = Date(startDate),
            endDate = Date(endDate),
            totalDistance = totalDistance,
            totalDuration = totalDuration,
            photoCount = photoCount,
            notes = notes,
            isTracking = isTracking
        )
    }

    private fun Journey.toEntity(): JourneyEntity {
        return JourneyEntity(
            id = id,
            tripId = tripId,
            startTime = startTime.time,
            endTime = endTime.time,
            distance = distance,
            coordinatesJson = Converters().coordinatesToJson(coordinates)
        )
    }

    private fun JourneyEntity.toDomain(): Journey {
        return Journey(
            id = id,
            tripId = tripId,
            startTime = Date(startTime),
            endTime = Date(endTime),
            distance = distance,
            coordinates = Converters().fromCoordinatesJson(coordinatesJson)
        )
    }

    private fun PhotoNote.toEntity(): PhotoNoteEntity {
        return PhotoNoteEntity(
            id = id,
            tripId = tripId,
            imagePath = imagePath,
            note = note,
            latitude = latitude,
            longitude = longitude,
            timestamp = timestamp.time
        )
    }

    private fun PhotoNoteEntity.toDomain(): PhotoNote {
        return PhotoNote(
            id = id,
            tripId = tripId,
            imagePath = imagePath,
            note = note,
            latitude = latitude,
            longitude = longitude,
            timestamp = Date(timestamp)
        )
    }
}