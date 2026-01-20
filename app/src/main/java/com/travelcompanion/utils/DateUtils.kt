package com.travelcompanion.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val dateTimeFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())

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
}

