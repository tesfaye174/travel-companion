package com.travelcompanion.domain.usecase

import com.travelcompanion.domain.model.Trip
import com.travelcompanion.domain.model.TripType
import com.travelcompanion.domain.repository.ITripRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import java.util.Date

/**
 * Unit tests for CreateTripUseCase.
 * Tests trip creation logic.
 */
@ExperimentalCoroutinesApi
class CreateTripUseCaseTest {

    @Mock
    private lateinit var mockRepository: ITripRepository

    private lateinit var useCase: CreateTripUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        useCase = CreateTripUseCase(mockRepository)
    }

    @Test
    fun `invoke with valid trip returns trip id`() = runTest {
        // Given
        val trip = Trip(
            id = 0,
            title = "Paris Trip",
            destination = "Paris, France",
            tripType = TripType.MULTI_DAY,
            startDate = Date(),
            endDate = Date(System.currentTimeMillis() + 86400000)
        )
        `when`(mockRepository.insertTrip(trip)).thenReturn(1L)

        // When
        val result = useCase(trip)

        // Then
        assertEquals(1L, result)
        verify(mockRepository).insertTrip(trip)
    }

    @Test
    fun `invoke with null end date succeeds`() = runTest {
        // Given
        val trip = Trip(
            id = 0,
            title = "Day Trip",
            destination = "Florence",
            tripType = TripType.DAY_TRIP,
            startDate = Date(),
            endDate = null
        )
        `when`(mockRepository.insertTrip(trip)).thenReturn(2L)

        // When
        val result = useCase(trip)

        // Then
        assertEquals(2L, result)
    }

    @Test
    fun `invoke calls repository insertTrip`() = runTest {
        // Given
        val trip = Trip(
            id = 0,
            title = "Test Trip",
            destination = "Test City",
            tripType = TripType.LOCAL,
            startDate = Date(),
            endDate = null
        )
        `when`(mockRepository.insertTrip(trip)).thenReturn(3L)

        // When
        useCase(trip)

        // Then
        verify(mockRepository).insertTrip(trip)
    }
}
