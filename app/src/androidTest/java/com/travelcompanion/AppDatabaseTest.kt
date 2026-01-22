package com.travelcompanion.data.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.travelcompanion.data.db.dao.TripDao
import com.travelcompanion.data.db.entities.TripEntity
import com.travelcompanion.domain.model.TripType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test for AppDatabase
 * Tests database operations on actual Android device/emulator
 */
@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {

    private lateinit var database: AppDatabase
    private lateinit var tripDao: TripDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).build()
        tripDao = database.tripDao()
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun insertAndRetrieveTrip() = runTest {
        // Given
        val trip = TripEntity(
            id = 0,
            destination = "Test Destination",
            startDate = System.currentTimeMillis(),
            endDate = System.currentTimeMillis() + 86400000,
            tripType = TripType.LEISURE,
            description = "Test trip description",
            coverPhotoUri = null
        )

        // When
        val insertedId = tripDao.insertTrip(trip)
        val retrievedTrip = tripDao.getTripById(insertedId).first()

        // Then
        assertNotNull(retrievedTrip)
        assertEquals("Test Destination", retrievedTrip?.destination)
        assertEquals(TripType.LEISURE, retrievedTrip?.tripType)
    }

    @Test
    fun getAllTripsReturnsAllInsertedTrips() = runTest {
        // Given
        val trips = listOf(
            TripEntity(
                id = 0,
                destination = "Paris",
                startDate = System.currentTimeMillis(),
                endDate = System.currentTimeMillis() + 86400000,
                tripType = TripType.LEISURE,
                description = "Trip 1",
                coverPhotoUri = null
            ),
            TripEntity(
                id = 0,
                destination = "London",
                startDate = System.currentTimeMillis(),
                endDate = System.currentTimeMillis() + 86400000,
                tripType = TripType.BUSINESS,
                description = "Trip 2",
                coverPhotoUri = null
            )
        )

        // When
        trips.forEach { tripDao.insertTrip(it) }
        val allTrips = tripDao.getAllTrips().first()

        // Then
        assertEquals(2, allTrips.size)
    }

    @Test
    fun updateTripModifiesExistingTrip() = runTest {
        // Given
        val trip = TripEntity(
            id = 0,
            destination = "Original Destination",
            startDate = System.currentTimeMillis(),
            endDate = System.currentTimeMillis() + 86400000,
            tripType = TripType.LEISURE,
            description = "Original description",
            coverPhotoUri = null
        )
        val insertedId = tripDao.insertTrip(trip)

        // When
        val updatedTrip = trip.copy(
            id = insertedId,
            destination = "Updated Destination",
            description = "Updated description"
        )
        tripDao.updateTrip(updatedTrip)
        val retrievedTrip = tripDao.getTripById(insertedId).first()

        // Then
        assertEquals("Updated Destination", retrievedTrip?.destination)
        assertEquals("Updated description", retrievedTrip?.description)
    }

    @Test
    fun deleteTripRemovesFromDatabase() = runTest {
        // Given
        val trip = TripEntity(
            id = 0,
            destination = "To Be Deleted",
            startDate = System.currentTimeMillis(),
            endDate = System.currentTimeMillis() + 86400000,
            tripType = TripType.LEISURE,
            description = "Will be deleted",
            coverPhotoUri = null
        )
        val insertedId = tripDao.insertTrip(trip)

        // When
        val tripToDelete = trip.copy(id = insertedId)
        tripDao.deleteTrip(tripToDelete)
        val retrievedTrip = tripDao.getTripById(insertedId).first()

        // Then
        assertEquals(null, retrievedTrip)
    }
}
