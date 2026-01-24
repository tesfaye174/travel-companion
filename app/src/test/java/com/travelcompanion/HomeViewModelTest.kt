package com.travelcompanion.ui.home

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for HomeViewModel QuickStats data class
 */
class HomeViewModelTest {

    @Test
    fun `QuickStats holds correct values`() {
        // Given
        val tripCount = 5
        val distance = 150.5f

        // When
        val stats = HomeViewModel.QuickStats(tripCount, distance)

        // Then
        assertEquals(tripCount, stats.totalTrips)
        assertEquals(distance, stats.totalDistance, 0.01f)
    }

    @Test
    fun `QuickStats with zero values`() {
        // Given/When
        val stats = HomeViewModel.QuickStats(0, 0f)

        // Then
        assertEquals(0, stats.totalTrips)
        assertEquals(0f, stats.totalDistance, 0.01f)
    }

    @Test
    fun `QuickStats with large values`() {
        // Given
        val largeCount = 999
        val largeDistance = 50000.75f

        // When
        val stats = HomeViewModel.QuickStats(largeCount, largeDistance)

        // Then
        assertEquals(largeCount, stats.totalTrips)
        assertEquals(largeDistance, stats.totalDistance, 0.01f)
    }
}
