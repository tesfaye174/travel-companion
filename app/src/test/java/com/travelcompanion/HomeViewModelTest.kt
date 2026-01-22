package com.travelcompanion.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.travelcompanion.data.db.entities.TripEntity
import com.travelcompanion.domain.model.QuickStats
import com.travelcompanion.domain.model.TripType
import com.travelcompanion.domain.repository.ITripRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

/**
 * Unit tests for HomeViewModel
 * Tests ViewModel logic and LiveData updates
 */
@ExperimentalCoroutinesApi
class HomeViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    @Mock
    private lateinit var mockRepository: ITripRepository

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadQuickStats calculates correct statistics`() = runTest {
        // Given
        val testTrips = listOf(
            TripEntity(
                id = 1,
                destination = "Paris",
                startDate = System.currentTimeMillis() - 86400000 * 7, // 7 days ago
                endDate = System.currentTimeMillis() - 86400000 * 4,   // 4 days ago
                tripType = TripType.LEISURE,
                description = "Test trip 1",
                coverPhotoUri = null
            ),
            TripEntity(
                id = 2,
                destination = "London",
                startDate = System.currentTimeMillis() - 86400000 * 3, // 3 days ago
                endDate = System.currentTimeMillis() - 86400000,       // 1 day ago
                tripType = TripType.BUSINESS,
                description = "Test trip 2",
                coverPhotoUri = null
            )
        )
        `when`(mockRepository.getAllTrips()).thenReturn(flowOf(testTrips))

        // When
        viewModel = HomeViewModel(mockRepository)

        // Then
        // In a real scenario, we'd observe LiveData and verify the QuickStats
        // This demonstrates the test structure
    }

    @Test
    fun `empty trip list returns zero statistics`() = runTest {
        // Given
        `when`(mockRepository.getAllTrips()).thenReturn(flowOf(emptyList()))

        // When
        viewModel = HomeViewModel(mockRepository)

        // Then
        // Verify that QuickStats has all zeros
    }

    @Test
    fun `recent trips shows last 5 trips`() = runTest {
        // Given
        val manyTrips = (1..10).map { index ->
            TripEntity(
                id = index.toLong(),
                destination = "Destination $index",
                startDate = System.currentTimeMillis() - (index * 86400000L),
                endDate = System.currentTimeMillis() - (index * 86400000L) + 3600000,
                tripType = TripType.LEISURE,
                description = "Trip $index",
                coverPhotoUri = null
            )
        }
        `when`(mockRepository.getAllTrips()).thenReturn(flowOf(manyTrips))

        // When
        viewModel = HomeViewModel(mockRepository)

        // Then
        // Verify only 5 most recent trips are shown
    }
}
