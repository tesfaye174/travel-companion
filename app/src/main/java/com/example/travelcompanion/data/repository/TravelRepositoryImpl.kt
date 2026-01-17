package com.example.travelcompanion.data.repository
import androidx.lifecycle.LiveData
import com.example.travelcompanion.data.local.*
import com.example.travelcompanion.domain.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.util.*
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class TravelRepository(
    private val tripDao: TripDao,
    private val journeyDao: JourneyDao,
    private val locationPointDao: LocationPointDao,
    private val photoDao: PhotoDao,
    private val noteDao: NoteDao
) {
    // Trip operations
    fun getAllTrips(): Flow<List<Trip>> = tripDao.getAllTrips()

    fun getUpcomingTrips(): Flow<List<Trip>> =
        tripDao.getUpcomingTrips(System.currentTimeMillis())

    fun getPastTrips(): Flow<List<Trip>> =
        tripDao.getPastTrips(System.currentTimeMillis())

    fun getTripById(tripId: Long): Flow<Trip?> = tripDao.getTripById(tripId)

    fun getActiveTrip(): Flow<Trip?> = tripDao.getActiveTrip()

    suspend fun insertTrip(trip: Trip): Long = tripDao.insertTrip(trip)

    suspend fun updateTrip(trip: Trip) = tripDao.updateTrip(trip)

    suspend fun deleteTrip(trip: Trip) = tripDao.deleteTrip(trip)

    suspend fun setActiveTrip(tripId: Long) {
        tripDao.deactivateAllTrips()
        tripDao.activateTrip(tripId)
    }

    // Journey operations
    fun getJourneysForTrip(tripId: Long): Flow<List<Journey>> =
        journeyDao.getJourneysForTrip(tripId)

    fun getActiveJourney(): Flow<Journey?> = journeyDao.getActiveJourney()

    fun getJourneyById(journeyId: Long): Flow<Journey?> =
        journeyDao.getJourneyById(journeyId)

    suspend fun startJourney(tripId: Long): Long {
        val journey = Journey(
            tripId = tripId,
            startTime = System.currentTimeMillis(),
            isActive = true
        )
        return journeyDao.insertJourney(journey)
    }

    suspend fun stopJourney(journeyId: Long) {
        journeyDao.stopJourney(journeyId, System.currentTimeMillis())
    }

    // Location operations
    fun getPointsForJourney(journeyId: Long): LiveData<List<LocationPoint>> =
        locationPointDao.getPointsForJourney(journeyId)

    suspend fun insertLocationPoint(point: LocationPoint): Long =
        locationPointDao.insertPoint(point)

    suspend fun getLastPointForJourney(journeyId: Long): LocationPoint? =
        locationPointDao.getLastPointForJourney(journeyId)

    // Photo operations
    fun getPhotosForTrip(tripId: Long): Flow<List<Photo>> =
        photoDao.getPhotosForTrip(tripId)

    fun getPhotosForJourney(journeyId: Long): Flow<List<Photo>> =
        photoDao.getPhotosForJourney(journeyId)

    suspend fun insertPhoto(photo: Photo): Long = photoDao.insertPhoto(photo)

    suspend fun deletePhoto(photo: Photo) = photoDao.deletePhoto(photo)

    // Note operations
    fun getNotesForTrip(tripId: Long): Flow<List<Note>> =
        noteDao.getNotesForTrip(tripId)

    fun getNoteById(noteId: Long): Flow<Note?> = noteDao.getNoteById(noteId)

    suspend fun insertNote(note: Note): Long = noteDao.insertNote(note)

    suspend fun updateNote(note: Note) = noteDao.updateNote(note)

    suspend fun deleteNote(note: Note) = noteDao.deleteNote(note)

    // Complex queries
    fun getTripDetails(tripId: Long): Flow<TripDetails?> {
        return combine(
            tripDao.getTripById(tripId),
            journeyDao.getJourneysForTrip(tripId),
            photoDao.getPhotosForTrip(tripId),
            noteDao.getNotesForTrip(tripId)
        ) { trip, journeys, photos, notes ->
            trip?.let {
                val allPoints = mutableListOf<LocationPoint>()
                var totalDistance = 0.0
                var totalDuration = 0L

                journeys.forEach { journey ->
                    val points = locationPointDao.getPointsForJourney(journey.id).value ?: emptyList()
                    allPoints.addAll(points)
                    totalDistance += calculateDistance(points)
                    totalDuration += (journey.endTime ?: System.currentTimeMillis()) - journey.startTime
                }

                TripDetails(
                    trip = it,
                    journeys = journeys,
                    photos = photos,
                    notes = notes,
                    totalDistance = totalDistance,
                    totalDuration = totalDuration,
                    locationPoints = allPoints
                )
            }
        }
    }

    suspend fun getTripStatistics(): TripStatistics {
        val trips = tripDao.getAllTrips()
        var totalTrips = 0
        var totalDistance = 0.0
        var totalDuration = 0L
        var photoCount = 0
        val tripsPerMonth = mutableMapOf<String, Int>()

        trips.collect { tripList ->
            totalTrips = tripList.size

            tripList.forEach { trip ->
                val journeys = journeyDao.getJourneysForTrip(trip.id)
                journeys.collect { journeyList ->
                    journeyList.forEach { journey ->
                        val points = locationPointDao.getPointsForJourney(journey.id).value ?: emptyList()
                        totalDistance += calculateDistance(points)
                        totalDuration += (journey.endTime ?: System.currentTimeMillis()) - journey.startTime
                    }
                }

                photoCount += photoDao.getPhotoCountForTrip(trip.id)

                val calendar = Calendar.getInstance()
                calendar.timeInMillis = trip.startDate
                val monthKey = "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}"
                tripsPerMonth[monthKey] = (tripsPerMonth[monthKey] ?: 0) + 1
            }
        }

        val averageSpeed = if (totalDuration > 0) {
            (totalDistance / (totalDuration / 3600000.0))
        } else 0.0

        return TripStatistics(
            totalTrips = totalTrips,
            totalDistance = totalDistance,
            totalDuration = totalDuration,
            tripsPerMonth = tripsPerMonth,
            averageSpeed = averageSpeed,
            photoCount = photoCount
        )
    }

    private fun calculateDistance(points: List<LocationPoint>): Double {
        if (points.size < 2) return 0.0

        var distance = 0.0
        for (i in 0 until points.size - 1) {
            distance += haversineDistance(
                points[i].latitude, points[i].longitude,
                points[i + 1].latitude, points[i + 1].longitude
            )
        }
        return distance
    }

    private fun haversineDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371.0 // Earth radius in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c
    }
}