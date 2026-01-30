package com.travelcompanion.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.travelcompanion.domain.model.Trip
import com.travelcompanion.domain.usecase.GetAllTripsUseCase
import com.travelcompanion.domain.usecase.TripStatsUseCase
import com.travelcompanion.utils.AppConstants
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
    private val getAllTripsUseCase: GetAllTripsUseCase,
    private val tripStatsUseCase: TripStatsUseCase
) : ViewModel() {

    // only show last recent trips on home screen
    val recentTrips: LiveData<List<Trip>> = getAllTripsUseCase()
        .map { trips -> trips.take(AppConstants.UI.RECENT_TRIPS_COUNT) }
        .asLiveData()

    private val _quickStats = MutableLiveData<QuickStats>()
    val quickStats: LiveData<QuickStats> = _quickStats

    init {
        loadQuickStats()
    }

    private fun loadQuickStats() {
        viewModelScope.launch {
            try {
                val stats = tripStatsUseCase()
                _quickStats.value = QuickStats(stats.totalTrips, stats.totalDistanceKm)
            } catch (e: Exception) {
                // handle error
            }
        }
    }

    data class QuickStats(
        val totalTrips: Int,
        val totalDistance: Float
    )
}


