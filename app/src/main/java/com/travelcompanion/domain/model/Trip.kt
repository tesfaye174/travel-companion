package com.travelcompanion.domain.model

import java.util.Date

/**
 * Represents a travel trip.
 */
data class Trip(
    val id: Long = 0,
    val name: String,
    val type: TripType,
    val startDate: Date,
    val endDate: Date,
    val distance: Float,
    val duration: Long,
    val notes: String? = null
)