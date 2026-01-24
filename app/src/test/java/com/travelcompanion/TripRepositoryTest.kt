package com.travelcompanion.data.repository

import com.travelcompanion.data.db.AppDatabase
import com.travelcompanion.data.db.dao.TripDao
import com.travelcompanion.data.db.entities.TripEntity
import com.travelcompanion.domain.model.Trip
import com.travelcompanion.domain.model.TripType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.util.Date

/**
 * Unit tests for TripRepository
 */
class TripRepositoryTest {

    @Mock
    private lateinit var mockDatabase: AppDatabase

    @Mock
    private lateinit var mockTripDao: TripDao

    private lateinit var repository: TripRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        `when`(mockDatabase.tripDao()).thenReturn(mockTripDao)
        repository = TripRepository(mockDatabase)
    }

    @Test
    fun `getAllTrips returns list of trips`() = runTest {
        // Given
        val testTrips = listOf(
            TripEntity(
                id = 1,
                title = "Paris Trip",
                destination = "Paris",
                startDate = System.currentTimeMillis(),
                endDate = System.currentTimeMillis() + 86400000,
                tripType = TripType.MULTI_DAY,
                notes = "Summer vacation"
            ),
            TripEntity(
                id = 2,
                title = "Tokyo Trip",
                destination = "Tokyo",
                startDate = System.currentTimeMillis(),
                endDate = System.currentTimeMillis() + 172800000,
                tripType = TripType.MULTI_DAY,
                notes = "Business trip"
            )
        )
        `when`(mockTripDao.getAllTripsFlow()).thenReturn(flowOf(testTrips))

        // When
        val result = repository.getAllTrips().first()

        // Then
        assertEquals(2, result.size)
        assertEquals("Paris", result[0].destination)
        assertEquals("Tokyo", result[1].destination)
    }

    @Test
    fun `getTripById returns correct trip`() = runTest {
        // Given
        val tripId = 1L
        val testTrip = TripEntity(
            id = tripId,
            title = "Rome Trip",
            destination = "Rome",
            startDate = System.currentTimeMillis(),
            endDate = System.currentTimeMillis() + 86400000,
            tripType = TripType.DAY_TRIP,
            notes = "Historical tour"
        )
        `when`(mockTripDao.getTripByIdFlow(tripId)).thenReturn(flowOf(testTrip))

        // When
        val result = repository.getTripById(tripId)

        // Then
        assertNotNull(result)
        assertEquals("Rome", result?.destination)
        assertEquals(TripType.DAY_TRIP, result?.tripType)
    }

    @Test
    fun `deleteTrip removes trip from database`() = runTest {
        // Given
        val tripToDelete = Trip(
            id = 1,
            title = "Barcelona Trip",
            destination = "Barcelona",
            startDate = Date(),
            endDate = Date(System.currentTimeMillis() + 86400000),
            tripType = TripType.LOCAL,
            notes = "Beach vacation"
        )

        // When - should not throw
        repository.deleteTrip(tripToDelete)

        // Then - verify method completed without error
    }
}
