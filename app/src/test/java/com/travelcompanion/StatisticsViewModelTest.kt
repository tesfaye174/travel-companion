package com.travelcompanion.ui.statistics

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.travelcompanion.domain.model.TripType
import com.travelcompanion.domain.repository.ITripRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

/**
 * Unit tests for StatisticsViewModel.
 * Tests statistics calculation and LiveData emissions.
 */
@ExperimentalCoroutinesApi
class StatisticsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    @Mock
    private lateinit var mockRepository: ITripRepository

    private lateinit var viewModel: StatisticsViewModel

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
    fun `getTotalDistance returns correct value`() = runTest {
        // Given
        val expectedDistance = 1500.5f
        `when`(mockRepository.getTotalDistance()).thenReturn(expectedDistance)
        `when`(mockRepository.getTotalDuration()).thenReturn(0L)
        `when`(mockRepository.getTripCount()).thenReturn(0)
        `when`(mockRepository.getMonthlyStats()).thenReturn(emptyList())
        `when`(mockRepository.getTripTypeStats()).thenReturn(emptyList())

        // When
        viewModel = StatisticsViewModel(mockRepository)

        // Then
        val stats = viewModel.statistics.value
        assertNotNull(stats)
        assertEquals(expectedDistance, stats?.totalDistance ?: 0f, 0.01f)
    }

    @Test
    fun `getTripCount returns correct value`() = runTest {
        // Given
        val expectedCount = 42
        `when`(mockRepository.getTotalDistance()).thenReturn(0f)
        `when`(mockRepository.getTotalDuration()).thenReturn(0L)
        `when`(mockRepository.getTripCount()).thenReturn(expectedCount)
        `when`(mockRepository.getMonthlyStats()).thenReturn(emptyList())
        `when`(mockRepository.getTripTypeStats()).thenReturn(emptyList())

        // When
        viewModel = StatisticsViewModel(mockRepository)

        // Then
        val stats = viewModel.statistics.value
        assertNotNull(stats)
        assertEquals(expectedCount, stats?.tripCount ?: 0)
    }

    @Test
    fun `monthly stats are correctly loaded`() = runTest {
        // Given
        val monthlyStats = listOf(
            ITripRepository.MonthlyStat("2026-01", 5, 250.0f, 36000000L),
            ITripRepository.MonthlyStat("2025-12", 3, 180.0f, 21600000L)
        )
        `when`(mockRepository.getTotalDistance()).thenReturn(430f)
        `when`(mockRepository.getTotalDuration()).thenReturn(57600000L)
        `when`(mockRepository.getTripCount()).thenReturn(8)
        `when`(mockRepository.getMonthlyStats()).thenReturn(monthlyStats)
        `when`(mockRepository.getTripTypeStats()).thenReturn(emptyList())

        // When
        viewModel = StatisticsViewModel(mockRepository)

        // Then
        val stats = viewModel.statistics.value
        assertNotNull(stats)
        assertEquals(2, stats?.monthlyStats?.size ?: 0)
    }

    @Test
    fun `trip type stats percentages are correct`() = runTest {
        // Given
        val tripTypeStats = listOf(
            ITripRepository.TripTypeStat(TripType.LOCAL, 5, 50f),
            ITripRepository.TripTypeStat(TripType.DAY_TRIP, 3, 30f),
            ITripRepository.TripTypeStat(TripType.MULTI_DAY, 2, 20f)
        )
        `when`(mockRepository.getTotalDistance()).thenReturn(0f)
        `when`(mockRepository.getTotalDuration()).thenReturn(0L)
        `when`(mockRepository.getTripCount()).thenReturn(10)
        `when`(mockRepository.getMonthlyStats()).thenReturn(emptyList())
        `when`(mockRepository.getTripTypeStats()).thenReturn(tripTypeStats)

        // When
        viewModel = StatisticsViewModel(mockRepository)

        // Then
        val stats = viewModel.statistics.value
        assertNotNull(stats)
        assertEquals(3, stats?.tripTypeStats?.size ?: 0)
        
        val localStat = stats?.tripTypeStats?.find { it.type == TripType.LOCAL }
        assertEquals(50f, localStat?.percentage ?: 0f, 0.01f)
    }

    @Test
    fun `empty statistics returns zeros`() = runTest {
        // Given
        `when`(mockRepository.getTotalDistance()).thenReturn(0f)
        `when`(mockRepository.getTotalDuration()).thenReturn(0L)
        `when`(mockRepository.getTripCount()).thenReturn(0)
        `when`(mockRepository.getMonthlyStats()).thenReturn(emptyList())
        `when`(mockRepository.getTripTypeStats()).thenReturn(emptyList())

        // When
        viewModel = StatisticsViewModel(mockRepository)

        // Then
        val stats = viewModel.statistics.value
        assertNotNull(stats)
        assertEquals(0f, stats?.totalDistance ?: -1f, 0.01f)
        assertEquals(0, stats?.tripCount ?: -1)
    }
}
