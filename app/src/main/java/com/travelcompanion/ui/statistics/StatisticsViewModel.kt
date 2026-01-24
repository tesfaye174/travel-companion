package com.travelcompanion.ui.statistics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelcompanion.domain.repository.ITripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
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
    private val repository: ITripRepository
) : ViewModel() {

    private val _totalTrips = MutableLiveData<Int>()
    val totalTrips: LiveData<Int> = _totalTrips

    private val _totalDistance = MutableLiveData<Float>()
    val totalDistance: LiveData<Float> = _totalDistance

    private val _totalDuration = MutableLiveData<Long>()
    val totalDuration: LiveData<Long> = _totalDuration

    private val _totalPhotos = MutableLiveData<Int>()
    val totalPhotos: LiveData<Int> = _totalPhotos

    private val _monthlyStats = MutableLiveData<List<ITripRepository.MonthlyStat>>()
    val monthlyStats: LiveData<List<ITripRepository.MonthlyStat>> = _monthlyStats

    private val _tripTypeStats = MutableLiveData<List<ITripRepository.TripTypeStat>>()
    val tripTypeStats: LiveData<List<ITripRepository.TripTypeStat>> = _tripTypeStats

    init {
        loadStatistics()
    }

    fun loadStatistics() {
        viewModelScope.launch {
            // Load total stats
            _totalTrips.value = repository.getTripCount()
            _totalDistance.value = repository.getTotalDistance()
            _totalDuration.value = repository.getTotalDuration()
            _totalPhotos.value = repository.getAllTrips().first().sumOf { it.photoCount }

            // Load monthly stats
            val rawMonthly = repository.getMonthlyStats()
            val byMonth = rawMonthly.associateBy { it.month.padStart(2, '0') }
            _monthlyStats.value = (1..12).map { m ->
                val key = m.toString().padStart(2, '0')
                val existing = byMonth[key]
                existing
                    ?: ITripRepository.MonthlyStat(
                        month = key,
                        tripCount = 0,
                        totalDistance = 0f,
                        totalDuration = 0L
                    )
            }

            // Load trip type stats
            _tripTypeStats.value = repository.getTripTypeStats()
        }
    }
}

