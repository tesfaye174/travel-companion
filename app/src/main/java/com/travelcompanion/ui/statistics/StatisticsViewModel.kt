package com.travelcompanion.ui.statistics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelcompanion.domain.usecase.TripStatsUseCase
import com.travelcompanion.domain.model.MonthlyStat
import com.travelcompanion.domain.model.TripTypeStat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for stats screen.
 * 
 * Loads trip analytics: total trips, distance, duration, monthly breakdown.
 * TODO: add caching to avoid reloading on config change
 */
@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val tripStatsUseCase: TripStatsUseCase
) : ViewModel() {

    // Semplice cache in memoria per evitare ricaricamenti su config change
    private var statsLoaded = false

    private val _totalTrips = MutableLiveData<Int>()
    val totalTrips: LiveData<Int> = _totalTrips

    private val _totalDistance = MutableLiveData<Float>()
    val totalDistance: LiveData<Float> = _totalDistance

    private val _totalDuration = MutableLiveData<Long>()
    val totalDuration: LiveData<Long> = _totalDuration

    private val _totalPhotos = MutableLiveData<Int>()
    val totalPhotos: LiveData<Int> = _totalPhotos

    private val _monthlyStats = MutableLiveData<List<MonthlyStat>>()
    val monthlyStats: LiveData<List<MonthlyStat>> = _monthlyStats

    private val _tripTypeStats = MutableLiveData<List<TripTypeStat>>()
    val tripTypeStats: LiveData<List<TripTypeStat>> = _tripTypeStats

    init {
        loadStatistics()
    }

    fun loadStatistics() {
        if (statsLoaded) return
        viewModelScope.launch {
            try {
                val stats = tripStatsUseCase()

                _totalTrips.value = stats.totalTrips
                _totalDistance.value = stats.totalDistanceKm
                _totalDuration.value = stats.totalDurationMs
                _totalPhotos.value = stats.totalPhotos

                _monthlyStats.value = (1..12).map { m ->
                    stats.monthlyStats.find { it.month == m } ?: com.travelcompanion.domain.model.MonthlyStat(
                        month = m,
                        tripCount = 0,
                        totalDistance = 0f,
                        totalDuration = 0L
                    )
                }

                _tripTypeStats.value = stats.tripTypeStats
                statsLoaded = true
            } catch (e: Exception) {
                // handle error
            }
        }
    }
}

