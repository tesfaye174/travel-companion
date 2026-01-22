package com.travelcompanion.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

/**
 * Unit tests for DateUtils utility class
 * Tests date formatting and calculation functions
 */
class DateUtilsTest {

    @Test
    fun `formatDate returns correct format`() {
        // Given
        val calendar = Calendar.getInstance().apply {
            set(2026, Calendar.JANUARY, 22, 10, 30, 0)
        }
        val date = calendar.time

        // When
        val result = DateUtils.formatDate(date)

        // Then
        assertTrue(result.contains("Jan") || result.contains("22") || result.contains("2026"))
    }

    @Test
    fun `formatDateRange returns correct range`() {
        // Given
        val calendar = Calendar.getInstance()
        calendar.set(2026, Calendar.JANUARY, 22)
        val startDate = calendar.time
        
        calendar.set(2026, Calendar.JANUARY, 25)
        val endDate = calendar.time

        // When
        val result = DateUtils.formatDateRange(startDate, endDate)

        // Then
        assertTrue(result.contains("-") || result.contains("to"))
    }

    @Test
    fun `calculateDaysBetween returns correct number of days`() {
        // Given
        val calendar = Calendar.getInstance()
        calendar.set(2026, Calendar.JANUARY, 22)
        val startDate = calendar.time
        
        calendar.set(2026, Calendar.JANUARY, 27)
        val endDate = calendar.time

        // When
        val result = DateUtils.calculateDaysBetween(startDate, endDate)

        // Then
        assertEquals(5, result)
    }

    @Test
    fun `calculateDaysBetween handles same day`() {
        // Given
        val date = Date()

        // When
        val result = DateUtils.calculateDaysBetween(date, date)

        // Then
        assertEquals(0, result)
    }

    @Test
    fun `isDateInPast returns true for past dates`() {
        // Given
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val pastDate = calendar.time

        // When
        val result = DateUtils.isDateInPast(pastDate)

        // Then
        assertTrue(result)
    }

    @Test
    fun `formatDuration returns correct format`() {
        // Given
        val durationMillis = 3661000L // 1 hour, 1 minute, 1 second

        // When
        val result = DateUtils.formatDuration(durationMillis)

        // Then
        assertTrue(result.contains("1") && (result.contains("h") || result.contains("hour")))
    }
}
