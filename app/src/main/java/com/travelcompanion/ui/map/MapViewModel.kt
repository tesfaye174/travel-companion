package com.travelcompanion.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelcompanion.domain.model.GeofenceArea
import com.travelcompanion.domain.model.GeofenceEvent
import com.travelcompanion.domain.model.Journey
import com.travelcompanion.domain.model.Trip
import com.travelcompanion.domain.repository.ITripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val repository: ITripRepository
) : ViewModel() {

    private val _currentLocation = MutableStateFlow<GeoPoint?>(null)
    val currentLocation: StateFlow<GeoPoint?> = _currentLocation.asStateFlow()

    // Hot, reactive flows shared with the Fragment. WhileSubscribed(5000) stops
    // collecting from Room when no observer is active (e.g. screen off) but keeps
    // the cached value through brief unsubscribe windows like configuration changes.
    val trips: StateFlow<List<Trip>> = repository.getAllTrips()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val journeys: StateFlow<List<Journey>> = repository.getAllJourneys()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val geofenceAreas: StateFlow<List<GeofenceArea>> = repository.getGeofenceAreas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val geofenceEvents: StateFlow<List<GeofenceEvent>> = repository.getGeofenceEvents()
        .map { list -> list.sortedByDescending { it.timestamp } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Kept as no-op callable entry points so existing Fragment code stays unchanged.
    fun loadTripsForMap() = Unit
    fun loadJourneysForMap() = Unit
    fun loadGeofenceAreas() = Unit
    fun loadGeofenceEvents() = Unit

    fun addGeofenceArea(id: String, name: String, lat: Double, lon: Double, radiusMeters: Float) {
        viewModelScope.launch {
            repository.upsertGeofenceArea(id, name, lat, lon, radiusMeters)
        }
    }

    fun updateCurrentLocation(lat: Double, lon: Double) {
        _currentLocation.value = GeoPoint(lat, lon)
    }
}
