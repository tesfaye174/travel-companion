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

    // UI State for quick stats. Derived from a SINGLE trips snapshot so trip count,
    // distance and duration are always mutually consistent (previously three separate
    // suspend reads could observe different DB states — e.g. demo seeding landing
    // between the count and the sum — yielding "0 trips" next to a non-zero distance).
    // Being Flow-based it also refreshes automatically when trips change.
    val quickStatsState: StateFlow<UiState<QuickStats>> = repository.getAllTrips()
        .map<List<Trip>, UiState<QuickStats>> { trips ->
            UiState.Success(
                QuickStats(
                    totalTrips = trips.size,
                    totalDistance = trips.sumOf { it.totalDistance.toDouble() }.toFloat(),
                    totalDuration = trips.sumOf { it.totalDuration }
                )
            )
        }
        .flowOn(Dispatchers.Default)
        .catch { e ->
            Timber.e(e, "Error loading quick stats")
            emit(UiState.Error(e, "Failed to load statistics"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UiState.Loading
        )

    /**
     * Kept for the swipe-to-refresh gesture. Recent trips and quick stats are now both
     * reactive Flows backed by Room, so they refresh automatically — no manual reload needed.
     */
    fun refresh() {
        // No-op: data is reactive. Method retained for the UI's pull-to-refresh callback.
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
