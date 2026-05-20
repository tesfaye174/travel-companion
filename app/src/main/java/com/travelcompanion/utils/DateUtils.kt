package com.travelcompanion.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Date formatting utilities.
 *
 * Note: SimpleDateFormat isn't thread-safe — these are accessed only from the main thread
 * or from coroutines that don't share the instance. Computed properties re-read
 * Locale.getDefault() each call so the app stays correct after a runtime locale change.
 */
object DateUtils {

    val dateFormat: SimpleDateFormat get() = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private val timeFormat: SimpleDateFormat get() = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val dateTimeFormat: SimpleDateFormat get() = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())

    fun formatDate(timestamp: Long): String {
        return dateFormat.format(Date(timestamp))
    }

    fun formatDate(date: Date): String {
        return dateFormat.format(date)
    }

    fun formatTime(timestamp: Long): String {
        return timeFormat.format(Date(timestamp))
    }

    fun formatDateTime(timestamp: Long): String {
        return dateTimeFormat.format(Date(timestamp))
    }

    fun formatDateTime(date: Date): String {
        return dateTimeFormat.format(date)
    }

    fun formatDateRange(startDate: Long, endDate: Long): String {
        val start = formatDate(startDate)
        val end = formatDate(endDate)
        return "$start - $end"
    }

    fun formatDateRange(start: Date, end: Date?): String {
        return if (end == null) {
            formatDate(start)
        } else {
            "${formatDate(start)} - ${formatDate(end)}"
        }
    }

    fun getDaysDifference(startDate: Long, endDate: Long): Int {
        val diff = endDate - startDate
        return (diff / (1000 * 60 * 60 * 24)).toInt() + 1
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

    fun getDaysDifference(start: Date, end: Date): Int {
        val diff = end.time - start.time
        return (diff / (1000 * 60 * 60 * 24)).toInt() + 1
    }

    /**
     * Formats distance in meters to human-readable format.
     */
    fun formatDistance(meters: Float): String {
        return when {
            meters >= 1000 -> String.format(Locale.getDefault(), "%.2f km", meters / 1000)
            else -> String.format(Locale.getDefault(), "%.0f m", meters)
        }
    }
}

