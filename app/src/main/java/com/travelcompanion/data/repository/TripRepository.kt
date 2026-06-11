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

/**
 * Implementazione del repository con Room. Gestisce le operazioni sul DB
 * e la conversione bidirezionale tra entità e modelli di dominio.
 * Gli errori vengono propagati al layer chiamante (ViewModel/UseCase).
 */
class TripRepository @Inject constructor(
    private val database: AppDatabase
) : ITripRepository {

    private val tripDao: TripDao = database.tripDao()
    private val journeyDao: JourneyDao = database.journeyDao()
    private val photoNoteDao: PhotoNoteDao = database.photoNoteDao()
    private val noteDao: NoteDao = database.noteDao()
    private val geofenceAreaDao: GeofenceAreaDao = database.geofenceAreaDao()
    private val geofenceEventDao: GeofenceEventDao = database.geofenceEventDao()

    // Istanza singola: Converters non ha stato, ma evitare allocazioni ripetute è comunque buona pratica
    private val converters = Converters()

    // La UI valida già i campi, ma il repository è l'unico punto da cui passano
    // tutte le scritture: rivalidare qui protegge anche i chiamanti futuri
    override suspend fun insertTrip(trip: Trip): Long {
        TripValidationUtils.validateForCreate(trip)
        return tripDao.insertTrip(trip.toEntity())
    }

    override suspend fun updateTrip(trip: Trip) {
        TripValidationUtils.validateForUpdate(trip)
        tripDao.updateTrip(trip.toEntity())
    }

    override suspend fun deleteTrip(trip: Trip) {
        TripValidationUtils.validateForDelete(trip)
        tripDao.deleteTrip(trip.toEntity())
    }

    override suspend fun deleteAllTrips() {
        tripDao.deleteAllTrips()
    }

    override suspend fun getTripById(id: Long): Trip? {
        return withContext(Dispatchers.IO) {
            tripDao.getTripByIdFlow(id).first()?.toDomain()
        }
    }

    override fun getTripByIdFlow(id: Long): Flow<Trip?> {
        return tripDao.getTripByIdFlow(id).map { it?.toDomain() }
    }

    override fun getAllTrips(): Flow<List<Trip>> {
        return tripDao.getAllTripsFlow()
            .map { entities -> entities.map { it.toDomain() } }
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
        return tripDao.getTotalDistance() ?: 0f
    }

    override suspend fun getTotalDuration(): Long {
        return tripDao.getTotalDuration() ?: 0L
    }

    override suspend fun getTripCount(): Int {
        return tripDao.getTripCount()
    }

    // Funzioni di conversione dominio <-> entità Room, private al repository
    private fun Trip.toEntity(): TripEntity {
        return TripEntity(
            id = id,
            title = title,
            destination = destination,
            tripType = tripType,
            startDate = startDate.time,
            endDate = endDate?.time ?: 0L,
            totalDistance = totalDistance,
            totalDuration = totalDuration,
            photoCount = photoCount,
            notes = notes,
            isTracking = isTracking
        )
    }

    private fun TripEntity.toDomain(): Trip {
        val endDateDomain = if (endDate == 0L || endDate == startDate) null else Date(endDate)
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
            coordinatesJson = converters.coordinatesToJson(coordinates)
        )
    }

    private fun JourneyEntity.toDomain(): Journey {
        return Journey(
            id = id,
            tripId = tripId,
            startTime = Date(startTime),
            endTime = if (endTime > 0L) Date(endTime) else null,
            distance = distance,
            coordinates = converters.fromCoordinatesJson(coordinatesJson)
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
            title = title,
            content = content,
            latitude = latitude,
            longitude = longitude,
            timestamp = timestamp.time,
            photoPath = photoPath
        )
    }

    private fun NoteEntity.toDomain(): Note {
        return Note(
            id = id,
            tripId = tripId,
            title = title,
            content = content,
            latitude = latitude,
            longitude = longitude,
            timestamp = Date(timestamp),
            photoPath = photoPath
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

}
