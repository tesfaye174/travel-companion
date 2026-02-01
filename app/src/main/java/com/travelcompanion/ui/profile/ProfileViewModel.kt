package com.travelcompanion.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelcompanion.domain.repository.ITripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ITripRepository
) : ViewModel() {

    private val _profileStats = MutableLiveData<ProfileStats>()
    val profileStats: LiveData<ProfileStats> = _profileStats

    init {
        loadProfileStats()
    }

    private fun loadProfileStats() {
        viewModelScope.launch {
            val totalTrips = repository.getTripCount()
            val totalDistance = repository.getTotalDistance()

            // In a real app, these would come from the repository
            _profileStats.value = ProfileStats(
                totalTrips = totalTrips,
                countriesVisited = calculateCountries(totalTrips),
                citiesVisited = calculateCities(totalTrips),
                totalDistance = totalDistance
            )
        }
    }

    private fun calculateCountries(trips: Int): Int {
        // Simplified calculation - in real app would query distinct countries
        return (trips / 3).coerceAtLeast(1)
    }

    private fun calculateCities(trips: Int): Int {
        // Simplified calculation - in real app would query distinct cities
        return trips.coerceAtLeast(1)
    }

    data class ProfileStats(
        val totalTrips: Int,
        val countriesVisited: Int,
        val citiesVisited: Int,
        val totalDistance: Float
    )
}
