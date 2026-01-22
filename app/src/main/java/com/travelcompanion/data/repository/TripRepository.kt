package com.travelcompanion.data.repository

import com.travelcompanion.data.db.AppDatabase
import com.travelcompanion.data.db.dao.JourneyDao
import com.travelcompanion.data.db.dao.NoteDao
import com.travelcompanion.data.db.dao.PhotoNoteDao
import com.travelcompanion.data.db.dao.TripDao
import com.travelcompanion.data.db.dao.GeofenceAreaDao
import com.travelcompanion.data.db.dao.GeofenceEventDao
import com.travelcompanion.data.db.entities.JourneyEntity
import com.travelcompanion.data.db.entities.NoteEntity
import com.travelcompanion.data.db.entities.PhotoNoteEntity
import com.travelcompanion.data.db.entities.TripEntity
import com.travelcompanion.data.db.entities.GeofenceAreaEntity
import com.travelcompanion.data.db.entities.GeofenceEventEntity
import com.travelcompanion.domain.model.*
import com.travelcompanion.domain.repository.ITripRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date
import com.travelcompanion.data.db.converters.Converters
import javax.inject.Inject

class TripRepository @Inject constructor(
    private val database: AppDatabase
) : ITripRepository {

    private val tripDao: TripDao = database.tripDao()
    private val journeyDao: JourneyDao = database.journeyDao()
    private val photoNoteDao: PhotoNoteDao = database.photoNoteDao()
    private val noteDao: NoteDao = database.noteDao()
    private val geofenceAreaDao: GeofenceAreaDao = database.geofenceAreaDao()
    private val geofenceEventDao: GeofenceEventDao = database.geofenceEventDao()

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
        val entity = tripDao.getTripByIdFlow(id).first()
        return entity?.toDomain()
    }

    override fun getAllTrips(): Flow<List<Trip>> {
        return tripDao.getAllTripsFlow().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getTripsByType(type: TripType): Flow<List<Trip>> {
        return tripDao.getTripsByTypeFlow(type).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getTripsBetweenDates(start: Date, end: Date): Flow<List<Trip>> {
        return tripDao.getTripsBetweenDatesFlow(start.time, end.time).map { entities ->
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

    override fun getAllJourneys(): Flow<List<Journey>> {
        return journeyDao.getAllJourneys().map { entities ->
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

    override suspend fun insertNote(note: Note): Long {
        return noteDao.insertNote(note.toEntity())
    }

    override suspend fun updateNote(note: Note) {
        noteDao.updateNote(note.toEntity())
    }

    override suspend fun deleteNote(note: Note) {
        noteDao.deleteNote(note.toEntity())
    }

    override fun getNotesByTripId(tripId: Long): Flow<List<Note>> {
        return noteDao.getNotesByTripId(tripId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun upsertGeofenceArea(id: String, name: String, lat: Double, lon: Double, radiusMeters: Float) {
        geofenceAreaDao.upsert(
            GeofenceAreaEntity(
                id = id,
                name = name,
                latitude = lat,
                longitude = lon,
                radiusMeters = radiusMeters
            )
        )
    }

    override fun getGeofenceAreas(): Flow<List<GeofenceArea>> {
        return geofenceAreaDao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getGeofenceEvents(): Flow<List<GeofenceEvent>> {
        return geofenceEventDao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getTotalDistance(): Float {
        return withContext(Dispatchers.IO) {
            tripDao.getTotalDistance() ?: 0f
        }
    }

    override suspend fun getTotalDuration(): Long {
        return withContext(Dispatchers.IO) {
            tripDao.getTotalDuration() ?: 0L
        }
    }

    override suspend fun getTripCount(): Int {
        return withContext(Dispatchers.IO) {
            tripDao.getTripCount()
        }
    }

    override suspend fun getMonthlyStats(): List<ITripRepository.MonthlyStat> {
        return withContext(Dispatchers.IO) {
            tripDao.getMonthlyStats().map { stat ->
                ITripRepository.MonthlyStat(
                    month = stat.month,
                    tripCount = stat.tripCount,
                    totalDistance = stat.totalDistance ?: 0f,
                    totalDuration = stat.totalDuration ?: 0L
                )
            }
        }
    }

    override suspend fun getTripTypeStats(): List<ITripRepository.TripTypeStat> {
        return withContext(Dispatchers.IO) {
            tripDao.getTripTypeStats().map { stat ->
                val tt = try {
                    com.travelcompanion.domain.model.TripType.valueOf(stat.type)
                } catch (ex: Exception) {
                    com.travelcompanion.domain.model.TripType.LOCAL
                }
                ITripRepository.TripTypeStat(
                    type = tt,
                    count = stat.count,
                    percentage = (stat.percentage ?: 0.0).toFloat()
                )
            }
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
            endDate = endDate?.time ?: startDate.time,
            totalDistance = totalDistance,
            totalDuration = totalDuration,
            photoCount = photoCount,
            notes = notes,
            isTracking = isTracking
        )
    }

    private fun TripEntity.toDomain(): Trip {
        val endDateDomain = if (endDate == startDate) null else Date(endDate)
        return Trip(
            id = id,
            title = title,
            destination = destination,
            tripType = tripType,
            startDate = Date(startDate),
            endDate = endDateDomain,
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
            endTime = endTime?.time ?: 0L,
            distance = distance,
            coordinatesJson = Converters().coordinatesToJson(coordinates)
        )
    }

    private fun JourneyEntity.toDomain(): Journey {
        return Journey(
            id = id,
            tripId = tripId,
            startTime = Date(startTime),
            endTime = if (endTime > 0L) Date(endTime) else null,
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

    private fun Note.toEntity(): NoteEntity {
        return NoteEntity(
            id = id,
            tripId = tripId,
            content = content,
            latitude = latitude,
            longitude = longitude,
            timestamp = timestamp.time
        )
    }

    private fun NoteEntity.toDomain(): Note {
        return Note(
            id = id,
            tripId = tripId,
            title = "",
            content = content,
            latitude = latitude,
            longitude = longitude,
            timestamp = Date(timestamp),
            photoPath = null
        )
    }

    private fun GeofenceAreaEntity.toDomain(): GeofenceArea {
        return GeofenceArea(
            id = id,
            name = name,
            latitude = latitude,
            longitude = longitude,
            radiusMeters = radiusMeters
        )
    }

    private fun GeofenceEventEntity.toDomain(): GeofenceEvent {
        return GeofenceEvent(
            id = id,
            geofenceId = geofenceId,
            transition = transition,
            timestamp = timestamp
        )
    }

    // Mapping entities to domain models for use in the application layer.
    // The mapping functions convert database entities to domain models
    // that are used throughout the application, ensuring a separation
    // between the data layer and the rest of the app.
    // Each mapping function is responsible for converting a single entity type.
    // These functions are used in the repository implementation to transform
    // data as it is read from or written to the database.
}

