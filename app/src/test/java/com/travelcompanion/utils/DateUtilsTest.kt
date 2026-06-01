package com.travelcompanion.utils

import org.junit.AfterClass
import org.junit.Assert.assertEquals
import org.junit.BeforeClass
import org.junit.Test
import java.util.Locale

/**
 * Locks in the formatting behaviour of [DateUtils], in particular the distance unit
 * conventions: meters below 1 km are shown in meters, values >= 1000 m are shown in km.
 * These conventions are what TrackingService and the trip UI rely on (Trip.totalDistance
 * is persisted in kilometers).
 */
class DateUtilsTest {

    companion object {
        private lateinit var previousLocale: Locale

        @BeforeClass
        @JvmStatic
        fun forceLocale() {
            // Force a deterministic locale so "%.2f" uses a dot decimal separator.
            previousLocale = Locale.getDefault()
            Locale.setDefault(Locale.US)
        }

        @AfterClass
        @JvmStatic
        fun restoreLocale() {
            Locale.setDefault(previousLocale)
        }
    }

    @Test
    fun `formatDistance shows meters below one kilometer`() {
        assertEquals("500 m", DateUtils.formatDistance(500f))
    }

    @Test
    fun `formatDistance shows kilometers at and above one kilometer`() {
        assertEquals("1.00 km", DateUtils.formatDistance(1000f))
        assertEquals("2.50 km", DateUtils.formatDistance(2500f))
    }

    @Test
    fun `formatDuration returns zero minutes for non-positive input`() {
        assertEquals("0m", DateUtils.formatDuration(0L))
        assertEquals("0m", DateUtils.formatDuration(-100L))
    }

    @Test
    fun `formatDuration formats hours and minutes`() {
        // 2h 5m
        assertEquals("2h 5m", DateUtils.formatDuration((2 * 60 + 5) * 60_000L))
        // exact hour drops the minutes component
        assertEquals("3h", DateUtils.formatDuration(3 * 60 * 60_000L))
        // sub-hour shows only minutes
        assertEquals("45m", DateUtils.formatDuration(45 * 60_000L))
    }

    @Test
    fun `getDaysDifference is inclusive of both endpoints`() {
        val dayMs = 24 * 60 * 60 * 1000L
        assertEquals(1, DateUtils.getDaysDifference(0L, 0L))
        assertEquals(3, DateUtils.getDaysDifference(0L, 2 * dayMs))
    }
}
