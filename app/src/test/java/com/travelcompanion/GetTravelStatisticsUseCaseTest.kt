package com.travelcompanion.domain.usecase

import com.travelcompanion.domain.model.TripType
import com.travelcompanion.domain.repository.ITripRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

/**
 * Unit tests for GetTravelStatisticsUseCase.
 * Tests statistics aggregation and calculations.
 */
@ExperimentalCoroutinesApi
class GetTravelStatisticsUseCaseTest {

    @Mock
    private lateinit var mockRepository: ITripRepository

    private lateinit var useCase: GetTravelStatisticsUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        useCase = GetTravelStatisticsUseCase(mockRepository)
    }

    @Test
    fun `invoke returns correct statistics`() = runTest {
        // Given
        `when`(mockRepository.getTotalDistance()).thenReturn(500f)
        `when`(mockRepository.getTotalDuration()).thenReturn(7200000L) // 2 hours
        `when`(mockRepository.getTripCount()).thenReturn(10)
        `when`(mockRepository.getMonthlyStats()).thenReturn(emptyList())
        `when`(mockRepository.getTripTypeStats()).thenReturn(emptyList())

        // When
        val result = useCase()

        // Then
        assertNotNull(result)
        assertEquals(500f, result.totalDistanceKm, 0.01f)
        assertEquals(7200000L, result.totalDurationMs)
        assertEquals(10, result.totalTrips)
    }

    @Test
    fun `average distance per trip is calculated correctly`() = runTest {
        // Given
        `when`(mockRepository.getTotalDistance()).thenReturn(1000f)
        `when`(mockRepository.getTotalDuration()).thenReturn(0L)
        `when`(mockRepository.getTripCount()).thenReturn(5)
        `when`(mockRepository.getMonthlyStats()).thenReturn(emptyList())
        `when`(mockRepository.getTripTypeStats()).thenReturn(emptyList())

        // When
        val result = useCase()

        // Then
        assertEquals(200f, result.averageDistancePerTrip, 0.01f)
    }

    @Test
    fun `average duration per trip is calculated correctly`() = runTest {
        // Given
        `when`(mockRepository.getTotalDistance()).thenReturn(0f)
        `when`(mockRepository.getTotalDuration()).thenReturn(10000L)
        `when`(mockRepository.getTripCount()).thenReturn(2)
        `when`(mockRepository.getMonthlyStats()).thenReturn(emptyList())
        `when`(mockRepository.getTripTypeStats()).thenReturn(emptyList())

        // When
        val result = useCase()

        // Then
        assertEquals(5000L, result.averageDurationPerTrip)
    }

    @Test
    fun `zero trips returns zero averages`() = runTest {
        // Given
        `when`(mockRepository.getTotalDistance()).thenReturn(0f)
        `when`(mockRepository.getTotalDuration()).thenReturn(0L)
        `when`(mockRepository.getTripCount()).thenReturn(0)
        `when`(mockRepository.getMonthlyStats()).thenReturn(emptyList())
        `when`(mockRepository.getTripTypeStats()).thenReturn(emptyList())

        // When
        val result = useCase()

        // Then
        assertEquals(0f, result.averageDistancePerTrip, 0.01f)
        assertEquals(0L, result.averageDurationPerTrip)
    }

    @Test
    fun `total duration hours is calculated correctly`() = runTest {
        // Given
        val twoHoursInMs = 2 * 60 * 60 * 1000L
        `when`(mockRepository.getTotalDistance()).thenReturn(0f)
        `when`(mockRepository.getTotalDuration()).thenReturn(twoHoursInMs)
        `when`(mockRepository.getTripCount()).thenReturn(1)
        `when`(mockRepository.getMonthlyStats()).thenReturn(emptyList())
        `when`(mockRepository.getTripTypeStats()).thenReturn(emptyList())

        // When
        val result = useCase()

        // Then
        assertEquals(2f, result.totalDurationHours, 0.01f)
    }

    @Test
    fun `monthly stats are included in result`() = runTest {
        // Given
        val monthlyStats = listOf(
            ITripRepository.MonthlyStat("2026-01", 3, 150f, 3600000L),
            ITripRepository.MonthlyStat("2025-12", 2, 100f, 2400000L)
        )
        `when`(mockRepository.getTotalDistance()).thenReturn(250f)
        `when`(mockRepository.getTotalDuration()).thenReturn(6000000L)
        `when`(mockRepository.getTripCount()).thenReturn(5)
        `when`(mockRepository.getMonthlyStats()).thenReturn(monthlyStats)
        `when`(mockRepository.getTripTypeStats()).thenReturn(emptyList())

        // When
        val result = useCase()

        // Then
        assertEquals(2, result.monthlyStats.size)
        assertEquals("2026-01", result.monthlyStats[0].month)
    }

    @Test
    fun `trip type stats are included in result`() = runTest {
        // Given
        val tripTypeStats = listOf(
            ITripRepository.TripTypeStat(TripType.LOCAL, 4, 40f),
            ITripRepository.TripTypeStat(TripType.DAY_TRIP, 3, 30f),
            ITripRepository.TripTypeStat(TripType.MULTI_DAY, 3, 30f)
        )
        `when`(mockRepository.getTotalDistance()).thenReturn(0f)
        `when`(mockRepository.getTotalDuration()).thenReturn(0L)
        `when`(mockRepository.getTripCount()).thenReturn(10)
        `when`(mockRepository.getMonthlyStats()).thenReturn(emptyList())
        `when`(mockRepository.getTripTypeStats()).thenReturn(tripTypeStats)

        // When
        val result = useCase()

        // Then
        assertEquals(3, result.tripTypeStats.size)
    }
}
