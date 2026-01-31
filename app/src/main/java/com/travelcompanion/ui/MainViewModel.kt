package com.travelcompanion.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelcompanion.data.local.entity.Trip
import com.travelcompanion.data.local.entity.TripType
import com.travelcompanion.data.repository.TripRepository
import com.travelcompanion.domain.usecase.AnalyzePredictionUseCase
import com.travelcompanion.service.TrackingService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: TripRepository,
    private val appContext: android.app.Application
) : ViewModel() {

    private val _uiState = MutableStateFlow(TravelUiState())
    val uiState: StateFlow<TravelUiState> = _uiState.asStateFlow()

    private var trackingService: TrackingService? = null
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            trackingService = (service as TrackingService.LocalBinder).getService()
        }
        override fun onServiceDisconnected(name: ComponentName?) { trackingService = null }
    }

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

    fun startTrip(name: String, dest: String, type: TripType) {
        viewModelScope.launch {
            val trip = Trip(name = name, destination = dest, type = type,
                startDate = System.currentTimeMillis(), endDate = 0, isActive = true)
            val id = repository.insertTrip(trip)
            startService(id)
        }
    }

    fun stopTrip() {
        viewModelScope.launch {
            val current = _uiState.value.trips.find { it.isActive }
            current?.let {
                val updated = it.copy(isActive = false, endDate = System.currentTimeMillis())
                repository.updateTrip(updated)
                stopService()
            }
        }
    }

    private fun startService(tripId: Long) {
        Intent(appContext, TrackingService::class.java).also { intent ->
            appContext.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
            appContext.startForegroundService(intent)
            trackingService?.startTracking(tripId)
        }
    }

    private fun stopService() {
        trackingService?.stopTracking()
        appContext.unbindService(serviceConnection)
    }
}

data class TravelUiState(
    val trips: List<Trip> = emptyList(),
    val prediction: com.travelcompanion.domain.usecase.PredictionResult? = null
)
