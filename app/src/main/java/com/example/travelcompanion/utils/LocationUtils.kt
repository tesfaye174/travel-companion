package com.example.travelcompanion.utils

import com.example.travelcompanion.domain.model.LocationPoint
import kotlin.math.*

object LocationUtils {

    fun calculateDistance(points: List<LocationPoint>): Double {
        if (points.size < 2) return 0.0

        var totalDistance = 0.0
        for (i in 0 until points.size - 1) {
            totalDistance += haversineDistance(
                points[i].latitude, points[i].longitude,
                points[i + 1].latitude, points[i + 1].longitude
            )
        }
        return totalDistance
    }

    fun haversineDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371.0 // Earth radius in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c
    }

    fun formatDistance(distanceKm: Double): String {
        return if (distanceKm < 1.0) {
            "${(distanceKm * 1000).toInt()} m"
        } else {
            "%.2f km".format(distanceKm)
        }
    }

    fun formatDuration(durationMillis: Long): String {
        val hours = durationMillis / 3600000
        val minutes = (durationMillis % 3600000) / 60000
        val seconds = (durationMillis % 60000) / 1000

        return if (hours > 0) {
            "%02d:%02d:%02d".format(hours, minutes, seconds)
        } else {
            "%02d:%02d".format(minutes, seconds)
        }
    }

    fun calculateSpeed(distanceKm: Double, durationMillis: Long): Double {
        if (durationMillis == 0L) return 0.0
        val hours = durationMillis / 3600000.0
        return distanceKm / hours
    }
}
