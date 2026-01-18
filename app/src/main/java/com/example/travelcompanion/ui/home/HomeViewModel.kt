package com.example.travelcompanion.ui.home

import app.cash.turbine.test
import com.example.travelcompanion.MainCoroutineRule
import com.example.travelcompanion.domain.model.Trip
import com.example.travelcompanion.domain.model.TripType
import com.example.travelcompanion.domain.repository.ITripRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.*

@ExperimentalCoroutinesApi
class HomeViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: HomeViewModel
    private lateinit var mockRepository: ITripRepository

    @Before
    fun setup() {
        mockRepository = mock()
        viewModel = HomeViewModel(mockRepository)
    }

    @Test
    fun `loadAllTrips should emit loading then success state`() = runTest {
        // Given
        val mockTrips = listOf(
            Trip(
                id = 1,
                title = "Test Trip",
                destination = "Test Destination",
                tripType = TripType.DAY_TRIP,
                startDate = Date(),
                endDate = Date()
            )
        )

        whenever(mockRepository.getAllTrips()).thenReturn(flowOf(mockTrips))

        // When & Then
        viewModel.uiState.test {
            // Initial state
            val initialState = awaitItem()
            assertTrue(initialState.trips.isEmpty())

            // Trigger load
            viewModel.selectTripType(null)

            // Loading state
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)

            // Success state
            val successState = awaitItem()
            assertFalse(successState.isLoading)
            assertEquals(1, successState.trips.size)
            assertEquals("Test Trip", successState.trips.first().title)
        }
    }

    @Test
    fun `when repository throws error, should emit error state`() = runTest {
        // Given
        val errorMessage = "Database error"
        whenever(mockRepository.getAllTrips()).thenReturn(
            flow { throw RuntimeException(errorMessage) }
        )

        // When & Then
        viewModel.uiState.test {
            skipItems(1) // Skip initial state

            viewModel.selectTripType(null)

            val errorState = awaitItem()
            assertTrue(errorState.isLoading)

            val finalState = awaitItem()
            assertNotNull(finalState.error)
            assertTrue(finalState.error!!.contains(errorMessage))
        }
    }

    @Test
    fun `searchTrips should filter trips based on query`() = runTest {
        // Given
        val trips = listOf(
            Trip(
                id = 1,
                title = "Rome Vacation",
                destination = "Rome, Italy",
                tripType = TripType.MULTI_DAY,
                startDate = Date(),
                endDate = Date(),
                notes = "Colosseum visit"
            ),
            Trip(
                id = 2,
                title = "Milan Business",
                destination = "Milan, Italy",
                tripType = TripType.LOCAL,
                startDate = Date(),
                endDate = Date(),
                notes = "Meeting with clients"
            )
        )

        whenever(mockRepository.getAllTrips()).thenReturn(flowOf(trips))

        // When & Then
        viewModel.uiState.test {
            skipItems(1)

            viewModel.selectTripType(null)
            awaitItem() // Wait for trips to load

            viewModel.searchTrips("Rome")

            val filteredState = awaitItem()
            assertEquals(1, filteredState.trips.size)
            assertEquals("Rome Vacation", filteredState.trips.first().title)
        }
    }

    @Test
    fun `deleteTrip should call repository delete`() = runTest {
        // Given
        val tripToDelete = Trip(
            id = 1,
            title = "Test Trip",
            destination = "Test",
            tripType = TripType.DAY_TRIP,
            startDate = Date(),
            endDate = Date()
        )

        // When
        viewModel.deleteTrip(tripToDelete)

        // Then
        verify(mockRepository).deleteTrip(tripToDelete)
    }

    @Test
    fun `clearError should reset error state`() = runTest {
        // Given
        whenever(mockRepository.getAllTrips()).thenReturn(
            flow { throw RuntimeException("Error") }
        )

        viewModel.uiState.test {
            skipItems(1)

            viewModel.selectTripType(null)
            awaitItem() // Error state

            // When
            viewModel.clearError()

            // Then
            val state = awaitItem()
            assertNull(state.error)
        }
    }
}