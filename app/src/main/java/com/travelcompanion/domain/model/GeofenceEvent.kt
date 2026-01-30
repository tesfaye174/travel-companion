package com.travelcompanion.domain.model

import java.util.Date

/**
 * Represents a geofence event (enter/exit).
 */
data class GeofenceEvent(
    val id: Long = 0,
    val geofenceId: String,
    val eventType: GeofenceEventType,
    val timestamp: Date
)

/**
 * Enum class representing geofence event types.
 */
enum class GeofenceEventType {
    ENTER,
    EXIT
}