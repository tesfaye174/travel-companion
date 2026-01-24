package com.travelcompanion.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.*

/**
 * Unit tests for DateUtils utility class
 * Tests date formatting and calculation functions
 */
class DateUtilsTest {

    @Test
    fun `formatDate returns correct format for Date object`() {
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
    fun `formatDate returns correct format for timestamp`() {
        // Given
        val calendar = Calendar.getInstance().apply {
            set(2026, Calendar.MARCH, 15, 14, 0, 0)
        }
        val timestamp = calendar.timeInMillis

        // When
        val result = DateUtils.formatDate(timestamp)

        // Then
        assertTrue(result.contains("Mar") || result.contains("15"))
    }

    @Test
    fun `formatDateRange returns correct range with two dates`() {
        // Given
        val calendar = Calendar.getInstance()
        calendar.set(2026, Calendar.JANUARY, 22)
        val startDate = calendar.time
        
        calendar.set(2026, Calendar.JANUARY, 25)
        val endDate = calendar.time

        // When
        val result = DateUtils.formatDateRange(startDate, endDate)

        // Then
        assertTrue(result.contains("-"))
    }

    @Test
    fun `formatDateRange handles null end date`() {
        // Given
        val calendar = Calendar.getInstance()
        calendar.set(2026, Calendar.FEBRUARY, 10)
        val startDate = calendar.time

        // When
        val result = DateUtils.formatDateRange(startDate, null)

        // Then
        assertTrue(result.contains("Feb") || result.contains("10"))
    }

    @Test
    fun `getDaysDifference returns correct number of days`() {
        // Given
        val calendar = Calendar.getInstance()
        calendar.set(2026, Calendar.JANUARY, 22, 0, 0, 0)
        val startDate = calendar.time
        
        calendar.set(2026, Calendar.JANUARY, 26, 0, 0, 0)
        val endDate = calendar.time

        // When
        val result = DateUtils.getDaysDifference(startDate, endDate)

        // Then
        assertEquals(5, result) // 22, 23, 24, 25, 26 = 5 days inclusive
    }

    @Test
    fun `getDaysDifference handles same day`() {
        // Given
        val date = Date()

        // When
        val result = DateUtils.getDaysDifference(date, date)

        // Then
        assertEquals(1, result) // Same day counts as 1
    }

    @Test
    fun `getDaysDifference works with timestamps`() {
        // Given
        val calendar = Calendar.getInstance()
        calendar.set(2026, Calendar.MARCH, 1, 12, 0, 0)
        val startTimestamp = calendar.timeInMillis
        
        calendar.set(2026, Calendar.MARCH, 3, 12, 0, 0)
        val endTimestamp = calendar.timeInMillis

        // When
        val result = DateUtils.getDaysDifference(startTimestamp, endTimestamp)

        // Then
        assertEquals(3, result) // 1, 2, 3 = 3 days
    }

    @Test
    fun `formatDuration returns correct format for hours and minutes`() {
        // Given
        val durationMillis = 3661000L // 1 hour, 1 minute, 1 second

        // When
        val result = DateUtils.formatDuration(durationMillis)

        // Then
        assertEquals("1h 1m", result)
    }

    @Test
    fun `formatDuration handles hours only`() {
        // Given
        val twoHours = 2 * 60 * 60 * 1000L

        // When
        val result = DateUtils.formatDuration(twoHours)

        // Then
        assertEquals("2h", result)
    }

    @Test
    fun `formatDuration handles minutes only`() {
        // Given
        val thirtyMinutes = 30 * 60 * 1000L

        // When
        val result = DateUtils.formatDuration(thirtyMinutes)

        // Then
        assertEquals("30m", result)
    }

    @Test
    fun `formatDuration handles zero duration`() {
        // Given
        val zeroDuration = 0L

        // When
        val result = DateUtils.formatDuration(zeroDuration)

        // Then
        assertEquals("0m", result)
    }

    @Test
    fun `formatTime returns correct format`() {
        // Given
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 14)
            set(Calendar.MINUTE, 30)
        }
        val timestamp = calendar.timeInMillis

        // When
        val result = DateUtils.formatTime(timestamp)

        // Then
        assertEquals("14:30", result)
    }

    @Test
    fun `formatDateTime returns correct format`() {
        // Given
        val calendar = Calendar.getInstance().apply {
            set(2026, Calendar.MAY, 20, 9, 15, 0)
        }
        val date = calendar.time

        // When
        val result = DateUtils.formatDateTime(date)

        // Then - check contains date parts and time
        assertTrue(result.contains("20") && result.contains("9:15"))
    }
}
