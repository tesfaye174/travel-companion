package com.travelcompanion.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelcompanion.domain.model.MonthlyStat
import com.travelcompanion.domain.model.Trip
import com.travelcompanion.domain.model.TripType
import com.travelcompanion.domain.model.TripTypeStat
import com.travelcompanion.domain.repository.ITripRepository
import com.travelcompanion.domain.usecase.AnalyzePredictionUseCase
import com.travelcompanion.domain.usecase.PredictionResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.Calendar
import javax.inject.Inject

enum class StatPeriod { THIS_MONTH, THIS_YEAR, ALL_TIME }

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val repository: ITripRepository,
    private val analyzePredictionUseCase: AnalyzePredictionUseCase
) : ViewModel() {

    private var currentPeriod = StatPeriod.THIS_YEAR
    val initialPeriod: StatPeriod get() = currentPeriod

    private val _totalTrips = MutableStateFlow(0)
    val totalTrips: StateFlow<Int> = _totalTrips.asStateFlow()

    private val _totalDistance = MutableStateFlow(0f)
    val totalDistance: StateFlow<Float> = _totalDistance.asStateFlow()

    private val _totalDuration = MutableStateFlow(0L)
    val totalDuration: StateFlow<Long> = _totalDuration.asStateFlow()

    private val _totalPhotos = MutableStateFlow(0)
    val totalPhotos: StateFlow<Int> = _totalPhotos.asStateFlow()

    private val _monthlyStats = MutableStateFlow<List<MonthlyStat>>(emptyList())
    val monthlyStats: StateFlow<List<MonthlyStat>> = _monthlyStats.asStateFlow()

    private val _tripTypeStats = MutableStateFlow<List<TripTypeStat>>(emptyList())
    val tripTypeStats: StateFlow<List<TripTypeStat>> = _tripTypeStats.asStateFlow()

    private val _prediction = MutableStateFlow<PredictionResult?>(null)
    val prediction: StateFlow<PredictionResult?> = _prediction.asStateFlow()

    init {
        loadStatistics()
    }

    fun setPeriod(period: StatPeriod) {
        if (currentPeriod == period) return
        currentPeriod = period
        loadStatistics()
    }

    fun loadStatistics() {
        viewModelScope.launch {
            try {
                // Compute aggregates off the main thread so the Statistics tab can paint
                // its frame even if the user opens it before charts are ready.
                val result = withContext(Dispatchers.Default) {
                    val allTrips = repository.getAllTrips().first()
                    val filtered = filterTripsByPeriod(allTrips, currentPeriod)

                    val monthlyCounts = filtered.groupBy { trip ->
                        val cal = Calendar.getInstance().apply { time = trip.startDate }
                        cal.get(Calendar.MONTH) + 1
                    }
                    val monthlyStats = (1..12).map { m ->
                        val tripsInMonth = monthlyCounts[m].orEmpty()
                        MonthlyStat(
                            month = m,
                            tripCount = tripsInMonth.size,
                            totalDistance = tripsInMonth.sumOf { it.totalDistance.toDouble() }.toFloat(),
                            totalDuration = tripsInMonth.sumOf { it.totalDuration }
                        )
                    }
                    val typeStats = TripType.entries.map { type ->
                        val ofType = filtered.filter { it.tripType == type }
                        TripTypeStat(
                            tripType = type,
                            tripCount = ofType.size,
                            totalDistance = ofType.sumOf { it.totalDistance.toDouble() }.toFloat(),
                            totalDuration = ofType.sumOf { it.totalDuration }
                        )
                    }
                    StatisticsBundle(
                        totalTrips = filtered.size,
                        totalDistance = filtered.sumOf { it.totalDistance.toDouble() }.toFloat(),
                        totalDuration = filtered.sumOf { it.totalDuration },
                        totalPhotos = filtered.sumOf { it.photoCount },
                        prediction = analyzePredictionUseCase.execute(filtered, emptyList()),
                        monthlyStats = monthlyStats,
                        tripTypeStats = typeStats
                    )
                }

                _totalTrips.value = result.totalTrips
                _totalDistance.value = result.totalDistance
                _totalDuration.value = result.totalDuration
                _totalPhotos.value = result.totalPhotos
                _prediction.value = result.prediction
                _monthlyStats.value = result.monthlyStats
                _tripTypeStats.value = result.tripTypeStats
            } catch (e: Exception) {
                Timber.e(e, "Error loading statistics")
            }
        }
    }

    private data class StatisticsBundle(
        val totalTrips: Int,
        val totalDistance: Float,
        val totalDuration: Long,
        val totalPhotos: Int,
        val prediction: PredictionResult,
        val monthlyStats: List<MonthlyStat>,
        val tripTypeStats: List<TripTypeStat>
    )

    private fun filterTripsByPeriod(trips: List<Trip>, period: StatPeriod): List<Trip> {
        if (period == StatPeriod.ALL_TIME) return trips
        val cal = Calendar.getInstance()
        when (period) {
            StatPeriod.THIS_MONTH -> {
                cal.set(Calendar.DAY_OF_MONTH, 1)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
            }
            StatPeriod.THIS_YEAR -> {
                cal.set(Calendar.DAY_OF_YEAR, 1)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
            }
            else -> return trips
        }
        val cutoff = cal.time
        return trips.filter { it.startDate >= cutoff }
    }

    override fun onCleared() {
        super.onCleared()
        Timber.d("StatisticsViewModel cleared")
    }
}
