package com.example.travelcompanion.data.repository

import com.example.travelcompanion.data.db.dao.*
import com.example.travelcompanion.data.db.entities.*
import com.example.travelcompanion.domain.model.*
import com.example.travelcompanion.domain.repository.TravelRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TravelRepositoryImpl(
    private val tripDao: TripDao,
    private val journeyDao: JourneyDao
) : TravelRepository {

    override fun getAllTrips(): Flow<List<Trip>> {
        return tripDao.getAllTrips().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getTripById(tripId: Long): Trip? {
        return tripDao.getTripById(tripId)?.toDomain()
    }

    override suspend fun insertTrip(trip: Trip): Long {
        return tripDao.insertTrip(trip.toEntity())
    }

    override suspend fun updateTrip(trip: Trip) {
        tripDao.updateTrip(trip.toEntity())
    }

    override suspend fun deleteTrip(trip: Trip) {
        tripDao.deleteTrip(trip.toEntity())
    }

    override fun getJourneysByTrip(tripId: Long): Flow<List<Journey>> {
        return journeyDao.getJourneysByTrip(tripId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertJourney(journey: Journey): Long {
        return journeyDao.insertJourney(journey.toEntity())
    }

    override suspend fun updateJourney(journey: Journey) {
        journeyDao.updateJourney(journey.toEntity())
    }

    override fun getPointsByJourney(journeyId: Long): Flow<List<Point>> {
        return journeyDao.getPointsByJourney(journeyId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertPoint(point: Point) {
        journeyDao.insertPoint(point.toEntity())
    }

    override suspend fun insertPhoto(photo: Photo) {
        journeyDao.insertPhoto(photo.toEntity())
    }

    override fun getPhotosByTrip(tripId: Long): Flow<List<Photo>> {
        return journeyDao.getPhotosByTrip(tripId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertNote(note: Note) {
        journeyDao.insertNote(note.toEntity())
    }

    override fun getNotesByTrip(tripId: Long): Flow<List<Note>> {
        return journeyDao.getNotesByTrip(tripId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getNotesByJourney(journeyId: Long): Flow<List<Note>> {
        return journeyDao.getNotesByJourney(journeyId).map { entities ->
            entities.map { it.toDomain() }
        }
    }
}

// Extension functions for mapping
fun TripEntity.toDomain() = Trip(id, destination, startDate, endDate, TripType.valueOf(type), isCompleted, totalDistance, totalTime)
fun Trip.toEntity() = TripEntity(id, destination, startDate, endDate, type.name, isCompleted, totalDistance, totalTime)

fun JourneyEntity.toDomain() = Journey(id, tripId, startTime, endTime, distance, time)
fun Journey.toEntity() = JourneyEntity(id, tripId, startTime, endTime, distance, time)

fun PointEntity.toDomain() = Point(id, journeyId, latitude, longitude, timestamp, speed, accuracy)
fun Point.toEntity() = PointEntity(id, journeyId, latitude, longitude, timestamp, speed, accuracy)

fun PhotoEntity.toDomain() = Photo(id, tripId, journeyId, pointId, filePath, timestamp)
fun Photo.toEntity() = PhotoEntity(id, tripId, journeyId, pointId, filePath, timestamp)

fun NoteEntity.toDomain() = Note(id, tripId, journeyId, pointId, content, timestamp)
fun Note.toEntity() = NoteEntity(id, tripId, journeyId, pointId, content, timestamp)
