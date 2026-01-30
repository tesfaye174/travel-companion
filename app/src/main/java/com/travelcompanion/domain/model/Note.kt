package com.travelcompanion.domain.model

import java.util.Date

/**
 * Represents a text note taken during a trip.
 */
data class Note(
    val id: Long = 0,
    val tripId: Long,
    val content: String,
    val timestamp: Date
)