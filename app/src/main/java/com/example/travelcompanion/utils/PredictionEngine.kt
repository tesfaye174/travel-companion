package com.example.travelcompanion.utils

import com.example.travelcompanion.ui.statistics.MonthlyStat

object PredictionEngine {

    fun predictNextMonthTrips(history: List<MonthlyStat>): Int {
        if (history.size < 2) return 1
        
        // Simple average as a heuristic prediction
        val average = history.map { it.count }.average()
        return average.toInt().coerceAtLeast(1)
    }

    fun predictNextMonthDistance(history: List<MonthlyStat>): Double {
        if (history.size < 2) return 50.0
        
        // Simple trend-based prediction
        val lastTrend = if (history.size >= 2) {
            history.last().distance - history[history.size - 2].distance
        } else 0.0
        
        return (history.last().distance + lastTrend).coerceAtLeast(10.0)
    }

    fun getSuggestion(predictedCount: Int, predictedDistance: Double): String {
        return when {
            predictedCount > 3 -> "You're a frequent traveler! Consider a multi-day trip next."
            predictedDistance < 50 -> "Try exploring more distant places. How about a day trip?"
            else -> "Perfect time for a local adventure."
        }
    }
}
