package com.travelcompanion.domain.model

import java.util.Date

/**
 * Represents a photo note taken during a trip.
 */
data class PhotoNote(
    val id: Long = 0,
    val tripId: Long,
    val photoPath: String,
    val note: String? = null,
    val timestamp: Date
)