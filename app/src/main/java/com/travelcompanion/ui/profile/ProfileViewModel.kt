package com.travelcompanion.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelcompanion.data.preferences.SettingsDataStore
import com.travelcompanion.domain.repository.ITripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ITripRepository,
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    private val _profileStats = MutableStateFlow<ProfileStats?>(null)
    val profileStats: StateFlow<ProfileStats?> = _profileStats.asStateFlow()

    val userName: StateFlow<String> = settingsDataStore.userNameFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val userEmail: StateFlow<String> = settingsDataStore.userEmailFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    init {
        loadProfileStats()
    }

    fun updateProfile(name: String, email: String) {
        viewModelScope.launch {
            settingsDataStore.setUserName(name)
            settingsDataStore.setUserEmail(email)
        }
    }

    fun clearProfile() {
        viewModelScope.launch {
            settingsDataStore.clearProfile()
        }
    }

    suspend fun getMemberSince(): Long = settingsDataStore.getOrInitMemberSince()

    private fun loadProfileStats() {
        viewModelScope.launch {
            val stats = withContext(Dispatchers.Default) {
                val totalTrips = repository.getTripCount()
                val totalDistance = repository.getTotalDistance()
                val trips = repository.getAllTrips().first()
                val distinctDestinations = trips.map { it.destination.trim() }.filter { it.isNotEmpty() }.toSet()
                val citiesVisited = distinctDestinations.size.coerceAtLeast(if (totalTrips > 0) 1 else 0)
                val countriesVisited = estimateCountries(distinctDestinations)
                ProfileStats(
                    totalTrips = totalTrips,
                    countriesVisited = countriesVisited,
                    citiesVisited = citiesVisited,
                    totalDistance = totalDistance
                )
            }
            _profileStats.value = stats
        }
    }

    private fun estimateCountries(destinations: Set<String>): Int {
        if (destinations.isEmpty()) return 0
        // Prova a estrarre il paese dalla stringa destinazione (es. "Roma, IT")
        val countries = destinations.mapNotNull { dest ->
            val parts = dest.split(",")
            if (parts.size >= 2) parts.last().trim().lowercase() else null
        }.toSet()
        return if (countries.isEmpty()) (destinations.size / 2).coerceAtLeast(1)
        else countries.size
    }

    data class ProfileStats(
        val totalTrips: Int,
        val countriesVisited: Int,
        val citiesVisited: Int,
        val totalDistance: Float
    )

    override fun onCleared() {
        super.onCleared()
        Timber.d("ProfileViewModel cleared")
    }
}
