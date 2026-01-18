package com.example.travelcompanion.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.travelcompanion.data.db.AppDatabase
import com.example.travelcompanion.data.db.entities.TripEntity
import com.example.travelcompanion.domain.model.TripType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*

@ExperimentalCoroutinesApi
class TripRepositoryTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: AppDatabase
    private lateinit var repository: TripRepository

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        repository = TripRepository(database)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `insertTrip should return valid id`() = runTest {
        // Given
        val trip = TripEntity(
            title = "Test Trip",
            destination = "Test Destination",
            tripType = TripType.DAY_TRIP,
            startDate = Date().time,
            endDate = Date().time + 86400000
        )

        // When
        val id = database.tripDao().insertTrip(trip)

        // Then
        assertTrue(id > 0)
    }

    @Test
    fun `getTripById should return inserted trip`() = runTest {
        // Given
        val trip = TripEntity(
            title = "Test Trip",
            destination = "Test Destination",
            tripType = TripType.DAY_TRIP,
            startDate = Date().time,
            endDate = Date().time + 86400000
        )

        val id = database.tripDao().insertTrip(trip)

        // When
        val retrievedTrip = database.tripDao().getTripById(id)

        // Then
        assertNotNull(retrievedTrip)
        assertEquals(trip.title, retrievedTrip?.title)
        assertEquals(trip.destination, retrievedTrip?.destination)
    }

    @Test
    fun `getAllTrips should return all trips`() = runTest {
        // Given
        val trip1 = TripEntity(
            title = "Trip 1",
            destination = "Destination 1",
            tripType = TripType.LOCAL,
            startDate = Date().time,
            endDate = Date().time + 86400000
        )

        val trip2 = TripEntity(
            title = "Trip 2",
            destination = "Destination 2",
            tripType = TripType.MULTI_DAY,
            startDate = Date().time,
            endDate = Date().time + 259200000
        )

        database.tripDao().insertTrip(trip1)
        database.tripDao().insertTrip(trip2)

        // When
        val allTrips = database.tripDao().getAllTrips()

        // Then
        allTrips.collect { trips ->
            assertEquals(2, trips.size)
        }
    }

    @Test
    fun `deleteTrip should remove trip from database`() = runTest {
        // Given
        val trip = TripEntity(
            title = "Test Trip",
            destination = "Test Destination",
            tripType = TripType.DAY_TRIP,
            startDate = Date().time,
            endDate = Date().time + 86400000
        )

        val id = database.tripDao().insertTrip(trip)
        val insertedTrip = database.tripDao().getTripById(id)!!

        // When
        database.tripDao().deleteTrip(insertedTrip)

        // Then
        val retrievedTrip = database.tripDao().getTripById(id)
        assertNull(retrievedTrip)
    }
}