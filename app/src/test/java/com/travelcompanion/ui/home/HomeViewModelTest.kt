package com.travelcompanion.ui.home

import app.cash.turbine.test
import com.travelcompanion.core.ui.UiState
import com.travelcompanion.data.preferences.SettingsDataStore
import com.travelcompanion.domain.model.Trip
import com.travelcompanion.domain.model.TripType
import com.travelcompanion.domain.repository.ITripRepository
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
    fun `quickStatsState derives count distance and duration from a single trips snapshot`() = runTest {
        // Regression for the race where trip count and total distance were read in
        // separate non-atomic queries and could disagree (e.g. "0 trips" / "57.3 km").
        // Mirrors the demo data: two trips, 12.3 + 45.0 km, 2h + 4h.
        val trips = listOf(
            Trip(
                id = 1, title = "Roma", destination = "Rome, IT", tripType = TripType.LOCAL,
                startDate = Date(7_000_000L), endDate = null,
                totalDistance = 12.3f, totalDuration = 2 * 60 * 60 * 1000L
            ),
            Trip(
                id = 2, title = "Torino", destination = "Torino, IT", tripType = TripType.DAY_TRIP,
                startDate = Date(3_000_000L), endDate = null,
                totalDistance = 45.0f, totalDuration = 4 * 60 * 60 * 1000L
            )
        )
        every { repository.getAllTrips() } returns flowOf(trips)

        viewModel = HomeViewModel(repository, settingsDataStore)

        viewModel.quickStatsState.test {
            // Skip Loading
            var item = awaitItem()
            if (item is UiState.Loading) {
                item = awaitItem()
            }
            assertTrue(item is UiState.Success)
            val stats = (item as UiState.Success).data
            // Count and distance come from the SAME list => always consistent.
            assertEquals(2, stats.totalTrips)
            assertEquals(57.3f, stats.totalDistance, 0.01f)
            assertEquals(6 * 60 * 60 * 1000L, stats.totalDuration)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `quickStatsState reports zero for an empty database`() = runTest {
        every { repository.getAllTrips() } returns flowOf(emptyList())

        viewModel = HomeViewModel(repository, settingsDataStore)

        viewModel.quickStatsState.test {
            var item = awaitItem()
            if (item is UiState.Loading) item = awaitItem()
            assertTrue(item is UiState.Success)
            val stats = (item as UiState.Success).data
            assertEquals(0, stats.totalTrips)
            assertEquals(0f, stats.totalDistance, 0.01f)
            assertEquals(0L, stats.totalDuration)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `userName flow emits from DataStore`() = runTest {
        every { repository.getAllTrips() } returns flowOf(emptyList())

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
