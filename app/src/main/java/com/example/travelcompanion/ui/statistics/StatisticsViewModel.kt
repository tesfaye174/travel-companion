package com.example.travelcompanion.ui.statistics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelcompanion.domain.repository.ITripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val repository: ITripRepository
) : ViewModel() {

    private val _totalTrips = MutableLiveData<Int>()
    val totalTrips: LiveData<Int> = _totalTrips

    private val _totalDistance = MutableLiveData<Float>()
    val totalDistance: LiveData<Float> = _totalDistance

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

            // Load monthly stats
            _monthlyStats.value = repository.getMonthlyStats()

            // Load trip type stats
            _tripTypeStats.value = repository.getTripTypeStats()
        }
    }
}