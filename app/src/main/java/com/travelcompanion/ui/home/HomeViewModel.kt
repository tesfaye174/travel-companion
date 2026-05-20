package com.travelcompanion.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelcompanion.core.ui.UiState
import com.travelcompanion.data.preferences.SettingsDataStore
import com.travelcompanion.domain.model.Trip
import com.travelcompanion.domain.repository.ITripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ITripRepository,
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    val userName: StateFlow<String> = settingsDataStore.userNameFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    // UI State for recent trips (last 3). Sort+take runs off the main thread via flowOn
    // so the Home screen can paint even if Room emits a large list.
    val recentTripsState: StateFlow<UiState<List<Trip>>> = repository.getAllTrips()
        .map { trips -> trips.sortedByDescending { it.startDate }.take(3) }
        .map<List<Trip>, UiState<List<Trip>>> { UiState.Success(it) }
        .flowOn(Dispatchers.Default)
        .catch { e ->
            Timber.e(e, "Error loading recent trips")
            emit(UiState.Error(e, "Failed to load recent trips"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UiState.Loading
        )

    // UI State for quick stats
    private val _quickStatsState = MutableStateFlow<UiState<QuickStats>>(UiState.Loading)
    val quickStatsState: StateFlow<UiState<QuickStats>> = _quickStatsState.asStateFlow()

    init {
        loadQuickStats()
    }

    /**
     * Loads quick statistics (trip count, total distance).
     */
    private fun loadQuickStats() {
        viewModelScope.launch {
            _quickStatsState.value = UiState.Loading
            try {
                val tripCount = repository.getTripCount()
                val totalDistance = repository.getTotalDistance()
                val totalDuration = repository.getTotalDuration()

                _quickStatsState.value = UiState.Success(
                    QuickStats(
                        totalTrips = tripCount,
                        totalDistance = totalDistance,
                        totalDuration = totalDuration
                    )
                )
            } catch (e: Exception) {
                Timber.e(e, "Error loading quick stats")
                _quickStatsState.value = UiState.Error(e, "Failed to load statistics")
            }
        }
    }

    /**
     * Refreshes all data (recent trips and quick stats).
     */
    fun refresh() {
        loadQuickStats()
        // Recent trips refresh automatically via Flow
    }

    /**
     * Data class for quick statistics.
     */
    data class QuickStats(
        val totalTrips: Int,
        val totalDistance: Float,
        val totalDuration: Long
    )
}


