package com.example.travelcompanion.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val dateTimeFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())

    fun formatDate(timestamp: Long): String {
        return dateFormat.format(Date(timestamp))
    }

    fun formatTime(timestamp: Long): String {
        return timeFormat.format(Date(timestamp))
    }

    fun formatDateTime(timestamp: Long): String {
        return dateTimeFormat.format(Date(timestamp))
    }

    fun formatDateRange(startDate: Long, endDate: Long): String {
        val start = formatDate(startDate)
        val end = formatDate(endDate)
        return "$start - $end"
    }

    fun getDaysDifference(startDate: Long, endDate: Long): Int {
        val diff = endDate - startDate
        return (diff / (1000 * 60 * 60 * 24)).toInt() + 1
    }
}
