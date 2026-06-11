package com.travelcompanion.utils

import org.junit.AfterClass
import org.junit.Assert.assertEquals
import org.junit.BeforeClass
import org.junit.Test
import java.util.Locale

/**
 * Fissa il comportamento di formattazione di [DateUtils], in particolare la
 * convenzione sulle distanze: sotto il chilometro mostro i metri, da 1000 m in su
 * i chilometri. È la stessa convenzione su cui contano il TrackingService e la UI
 * dei viaggi (Trip.totalDistance è salvata in chilometri).
 */
class DateUtilsTest {

    companion object {
        private lateinit var previousLocale: Locale

        @BeforeClass
        @JvmStatic
        fun forceLocale() {
            // Locale fisso e deterministico, così "%.2f" usa il punto come separatore decimale.
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
        // l'ora esatta non mostra i minuti
        assertEquals("3h", DateUtils.formatDuration(3 * 60 * 60_000L))
        // sotto l'ora si vedono solo i minuti
        assertEquals("45m", DateUtils.formatDuration(45 * 60_000L))
    }
}
