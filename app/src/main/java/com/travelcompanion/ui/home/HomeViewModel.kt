package com.travelcompanion.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.travelcompanion.domain.model.Trip
import com.travelcompanion.domain.repository.ITripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for home/dashboard screen.
 * Shows recent trips and quick stats.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ITripRepository
) : ViewModel() {

    // only show last 3 trips on home screen
    val recentTrips: LiveData<List<Trip>> = repository.getAllTrips()
        .map { trips -> trips.take(3) }
        .asLiveData()

    private val _quickStats = MutableLiveData<QuickStats>()
    val quickStats: LiveData<QuickStats> = _quickStats

    init {
        loadQuickStats()
    }

    private fun loadQuickStats() {
        viewModelScope.launch {
            val tripCount = repository.getTripCount()
            val totalDistance = repository.getTotalDistance()
            _quickStats.value = QuickStats(tripCount, totalDistance)
        }
    }

    data class QuickStats(
        val totalTrips: Int,
        val totalDistance: Float
    )
}


