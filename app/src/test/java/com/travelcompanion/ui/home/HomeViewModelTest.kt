package com.travelcompanion.ui.home

import app.cash.turbine.test
import com.travelcompanion.core.ui.UiState
import com.travelcompanion.data.preferences.SettingsDataStore
import com.travelcompanion.domain.model.Trip
import com.travelcompanion.domain.model.TripType
import com.travelcompanion.domain.repository.ITripRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: ITripRepository
    private lateinit var settingsDataStore: SettingsDataStore
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        settingsDataStore = mockk()

        every { settingsDataStore.userNameFlow } returns flowOf("Test User")
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `recentTripsState emits success with sorted trips limited to 3`() = runTest {
        val trips = (1..5).map { i ->
            Trip(
                id = i.toLong(),
                title = "Trip $i",
                destination = "City $i",
                tripType = TripType.LOCAL,
                startDate = Date(i * 1000000L),
                endDate = null
            )
        }
        every { repository.getAllTrips() } returns flowOf(trips)
        coEvery { repository.getTripCount() } returns 5
        coEvery { repository.getTotalDistance() } returns 100f
        coEvery { repository.getTotalDuration() } returns 5000L

        viewModel = HomeViewModel(repository, settingsDataStore)

        viewModel.recentTripsState.test {
            // Skip Loading initial value
            val first = awaitItem()
            if (first is UiState.Loading) {
                val success = awaitItem()
                assertTrue(success is UiState.Success)
                assertEquals(3, (success as UiState.Success).data.size)
            } else {
                assertTrue(first is UiState.Success)
                assertEquals(3, (first as UiState.Success).data.size)
            }
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `quickStatsState emits correct stats`() = runTest {
        every { repository.getAllTrips() } returns flowOf(emptyList())
        coEvery { repository.getTripCount() } returns 10
        coEvery { repository.getTotalDistance() } returns 250.5f
        coEvery { repository.getTotalDuration() } returns 86400000L

        viewModel = HomeViewModel(repository, settingsDataStore)

        viewModel.quickStatsState.test {
            // Skip Loading
            var item = awaitItem()
            if (item is UiState.Loading) {
                item = awaitItem()
            }
            assertTrue(item is UiState.Success)
            val stats = (item as UiState.Success).data
            assertEquals(10, stats.totalTrips)
            assertEquals(250.5f, stats.totalDistance, 0.01f)
            assertEquals(86400000L, stats.totalDuration)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `userName flow emits from DataStore`() = runTest {
        every { repository.getAllTrips() } returns flowOf(emptyList())
        coEvery { repository.getTripCount() } returns 0
        coEvery { repository.getTotalDistance() } returns 0f
        coEvery { repository.getTotalDuration() } returns 0L

        viewModel = HomeViewModel(repository, settingsDataStore)

        viewModel.userName.test {
            val name = awaitItem()
            // Initial value "" or "Test User" depending on timing
            val finalName = if (name.isEmpty()) awaitItem() else name
            assertEquals("Test User", finalName)
            cancelAndConsumeRemainingEvents()
        }
    }
}
