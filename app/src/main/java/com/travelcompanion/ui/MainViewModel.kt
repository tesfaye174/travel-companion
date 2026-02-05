package com.travelcompanion.ui

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelcompanion.domain.model.Trip
import com.travelcompanion.domain.model.TripType
import com.travelcompanion.domain.repository.ITripRepository
import com.travelcompanion.domain.usecase.AnalyzePredictionUseCase
import com.travelcompanion.ui.tracking.TrackingService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: ITripRepository,
    private val appContext: android.app.Application
) : ViewModel() {

    private val _uiState = MutableStateFlow(TravelUiState())
    val uiState: StateFlow<TravelUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            repository.getAllTrips().collect { trips ->
                // Recupera le locations per la predizione (semplificato qui)
                val prediction = AnalyzePredictionUseCase().execute(trips, emptyList())
                _uiState.update { it.copy(trips = trips, prediction = prediction) }
            }
        }
    }

    /**
     * Avvia un nuovo viaggio e il tracking associato.
     * Esempio di commento didattico: questa funzione crea un nuovo oggetto Trip, lo salva nel database
     * e avvia il servizio di tracking. Tutte le operazioni sono svolte in coroutine per non bloccare la UI.
     */
    fun startTrip(name: String, dest: String, type: TripType) {
        viewModelScope.launch {
            val trip = Trip(
                title = name,
                destination = dest,
                tripType = type,
                startDate = Date(),
                endDate = null,
                isTracking = true
            )
            val id = repository.insertTrip(trip)
            startTrackingService(id)
        }
    }

    /**
     * Termina il viaggio attualmente in corso (se presente) e aggiorna il database.
     * Esempio di commento didattico: la funzione cerca il viaggio in tracking, lo aggiorna e ferma il servizio.
     */
    fun stopTrip() {
        viewModelScope.launch {
            val current = _uiState.value.trips.find { it.isTracking }
            current?.let {
                val updated = it.copy(isTracking = false, endDate = Date())
                repository.updateTrip(updated)
                stopTrackingService()
            }
        }
    }

    private fun startTrackingService(tripId: Long) {
        val intent = Intent(appContext, TrackingService::class.java).apply {
            putExtra(TrackingService.EXTRA_TRIP_ID, tripId)
        }
        appContext.startForegroundService(intent)
    }

    private fun stopTrackingService() {
        val intent = Intent(appContext, TrackingService::class.java)
        appContext.stopService(intent)
    }
}

data class TravelUiState(
    val trips: List<Trip> = emptyList(),
    val prediction: com.travelcompanion.domain.usecase.PredictionResult? = null
)
