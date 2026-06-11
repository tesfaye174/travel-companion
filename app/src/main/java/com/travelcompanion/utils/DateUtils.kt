package com.travelcompanion.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Utility per la formattazione delle date. I formati vengono rigenerati a ogni chiamata per seguire i cambi di locale.
 */
object DateUtils {

    // pubblico perché NewTripFragment lo usa anche per il parsing dei campi data
    val dateFormat: SimpleDateFormat get() = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    fun formatDate(date: Date): String {
        return dateFormat.format(date)
    }

    fun formatDateRange(start: Date, end: Date?): String {
        return if (end == null) {
            formatDate(start)
        } else {
            "${formatDate(start)} - ${formatDate(end)}"
        }
    }

    fun formatDuration(durationMillis: Long): String {
        if (durationMillis <= 0) return "0m"
        val totalMinutes = (durationMillis / 1000 / 60).toInt()
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60
        return if (hours > 0) {
            if (minutes > 0) "${hours}h ${minutes}m" else "${hours}h"
        } else {
            "${minutes}m"
        }
    }

    fun formatDistance(meters: Float): String {
        return when {
            meters >= 1000 -> String.format(Locale.getDefault(), "%.2f km", meters / 1000)
            else -> String.format(Locale.getDefault(), "%.0f m", meters)
        }
    }
}
