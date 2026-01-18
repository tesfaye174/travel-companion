package com.example.travelcompanion.domain.model

import java.util.Date

data class PhotoNote(
    val id: Long = 0,
    val tripId: Long,
    val imagePath: String,
    val note: String,
    val latitude: Double?,
    val longitude: Double?,
    val timestamp: Date = Date()
)