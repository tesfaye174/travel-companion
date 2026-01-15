package com.example.travelcompanion.ui.stats

import androidx.lifecycle.*
import com.example.travelcompanion.domain.model.Trip
import com.example.travelcompanion.domain.repository.TravelRepository
import com.example.travelcompanion.utils.PredictionEngine
import kotlinx.coroutines.flow.map

class StatsViewModel(private val repository: TravelRepository) : ViewModel() {

    private val _stats = repository.getAllTrips().map { trips ->
        trips.groupBy { 
            val calendar = java.util.Calendar.getInstance()
            calendar.timeInMillis = it.startDate
            calendar.get(java.util.Calendar.MONTH)
        }.map { (month, monthTrips) ->
            MonthlyStat(
                month = month,
                distance = monthTrips.sumOf { it.totalDistance },
                count = monthTrips.size
            )
        }.sortedBy { it.month }
    }

    val tripStats: LiveData<List<MonthlyStat>> = _stats.asLiveData()

    val forecast: LiveData<Forecast> = _stats.map { stats ->
        val nextTrips = PredictionEngine.predictNextMonthTrips(stats)
        val nextDist = PredictionEngine.predictNextMonthDistance(stats)
        Forecast(
            predictedCount = nextTrips,
            predictedDistance = nextDist,
            suggestion = PredictionEngine.getSuggestion(nextTrips, nextDist)
        )
    }.asLiveData()
}

data class Forecast(
    val predictedCount: Int,
    val predictedDistance: Double,
    val suggestion: String
)

data class MonthlyStat(
    val month: Int,
    val distance: Double,
    val count: Int
)

class StatsViewModelFactory(private val repository: TravelRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StatsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
