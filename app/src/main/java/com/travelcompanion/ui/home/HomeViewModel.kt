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

    // UI State per i trip recenti (ultimi 3). Ordinamento e limite sono su thread in background
    // così la Home si disegna subito anche con liste lunghe dal database
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

    // UI State per le statistiche veloci. Derivate da un UNICO snapshot di trip, così
    // il conteggio, la distanza e la durata sono sempre coerenti tra loro. Essendo basato
    // su Flow si aggiorna automaticamente quando i dati cambiano
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
     * Mantenuto per il gesto di refresh. I dati sono reattivi e si aggiornano da soli.
     */
    fun refresh() {
        // No-op: i dati si aggiornano automaticamente via Flow
    }

    data class QuickStats(
        val totalTrips: Int,
        val totalDistance: Float,
        val totalDuration: Long
    )
}
