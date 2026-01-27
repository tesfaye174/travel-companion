package com.travelcompanion.domain.model

import java.util.Date

object TripValidationUtils {
    fun validateForCreate(trip: Trip) {
        require(trip.title.isNotBlank()) { "Trip title cannot be empty" }
        require(trip.destination.isNotBlank()) { "Destination cannot be empty" }
        trip.endDate?.let { endDate ->
            require(endDate >= trip.startDate) { "End date must be after start date" }
        }
    }
    fun validateForUpdate(trip: Trip) {
        require(trip.id > 0) { "Trip must have valid ID for update" }
        require(trip.title.isNotBlank()) { "Title cannot be empty" }
        require(trip.destination.isNotBlank()) { "Destination cannot be empty" }
    }
    fun validateForDelete(trip: Trip) {
        require(trip.id > 0) { "Trip must have valid ID" }
    }
}
