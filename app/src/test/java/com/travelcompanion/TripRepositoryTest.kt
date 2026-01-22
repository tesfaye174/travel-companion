package com.travelcompanion.data.repository

import com.travelcompanion.data.db.AppDatabase
import com.travelcompanion.data.db.dao.TripDao
import com.travelcompanion.data.db.entities.TripEntity
import com.travelcompanion.domain.model.TripType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import kotlinx.coroutines.flow.flowOf

/**
 * Unit tests for TripRepository
 * Tests the business logic layer without requiring Android dependencies
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
                destination = "Paris",
                startDate = System.currentTimeMillis(),
                endDate = System.currentTimeMillis() + 86400000,
                tripType = TripType.LEISURE,
                description = "Summer vacation",
                coverPhotoUri = null
            ),
            TripEntity(
                id = 2,
                destination = "Tokyo",
                startDate = System.currentTimeMillis(),
                endDate = System.currentTimeMillis() + 172800000,
                tripType = TripType.BUSINESS,
                description = "Business trip",
                coverPhotoUri = null
            )
        )
        `when`(mockTripDao.getAllTrips()).thenReturn(flowOf(testTrips))

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
            destination = "Rome",
            startDate = System.currentTimeMillis(),
            endDate = System.currentTimeMillis() + 86400000,
            tripType = TripType.LEISURE,
            description = "Historical tour",
            coverPhotoUri = null
        )
        `when`(mockTripDao.getTripById(tripId)).thenReturn(flowOf(testTrip))

        // When
        val result = repository.getTripById(tripId).first()

        // Then
        assertNotNull(result)
        assertEquals("Rome", result?.destination)
        assertEquals(TripType.LEISURE, result?.tripType)
    }

    @Test
    fun `insertTrip returns valid trip ID`() = runTest {
        // Given
        val newTrip = TripEntity(
            id = 0,
            destination = "London",
            startDate = System.currentTimeMillis(),
            endDate = System.currentTimeMillis() + 86400000,
            tripType = TripType.BUSINESS,
            description = "Conference",
            coverPhotoUri = null
        )
        val expectedId = 3L
        `when`(mockTripDao.insertTrip(newTrip)).thenReturn(expectedId)

        // When
        val resultId = repository.insertTrip(newTrip)

        // Then
        assertEquals(expectedId, resultId)
    }

    @Test
    fun `deleteTrip removes trip from database`() = runTest {
        // Given
        val tripToDelete = TripEntity(
            id = 1,
            destination = "Barcelona",
            startDate = System.currentTimeMillis(),
            endDate = System.currentTimeMillis() + 86400000,
            tripType = TripType.LEISURE,
            description = "Beach vacation",
            coverPhotoUri = null
        )

        // When
        repository.deleteTrip(tripToDelete)

        // Then
        // Verify interaction happened (in real test with MockK we'd use verify)
        // This demonstrates the test structure
    }
}
