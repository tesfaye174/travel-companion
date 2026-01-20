package com.travelcompanion.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelcompanion.domain.model.GeofenceArea
import com.travelcompanion.domain.model.GeofenceEvent
import com.travelcompanion.domain.model.Journey
import com.travelcompanion.domain.model.Trip
import com.travelcompanion.domain.repository.ITripRepository
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val repository: ITripRepository
) : ViewModel() {

    private val _trips = MutableLiveData<List<Trip>>()
    val trips: LiveData<List<Trip>> = _trips

    private val _currentLocation = MutableLiveData<LatLng?>()
    val currentLocation: LiveData<LatLng?> = _currentLocation

    private val _journeys = MutableLiveData<List<Journey>>()
    val journeys: LiveData<List<Journey>> = _journeys

    private val _geofenceAreas = MutableLiveData<List<GeofenceArea>>()
    val geofenceAreas: LiveData<List<GeofenceArea>> = _geofenceAreas

    private val _geofenceEvents = MutableLiveData<List<GeofenceEvent>>()
    val geofenceEvents: LiveData<List<GeofenceEvent>> = _geofenceEvents

    fun loadTripsForMap() {
        viewModelScope.launch {
            repository.getAllTrips().collect { tripsList ->
                _trips.value = tripsList
            }
        }
    }

    fun loadJourneysForMap() {
        viewModelScope.launch {
            repository.getAllJourneys().collect { list ->
                _journeys.value = list
            }
        }
    }

    fun loadGeofenceAreas() {
        viewModelScope.launch {
            repository.getGeofenceAreas().collect { list ->
                _geofenceAreas.value = list
            }
        }
    }

    fun loadGeofenceEvents() {
        viewModelScope.launch {
            repository.getGeofenceEvents().collect { list ->
                _geofenceEvents.value = list.sortedByDescending { it.timestamp }
            }
        }
    }

    fun addGeofenceArea(id: String, name: String, lat: Double, lon: Double, radiusMeters: Float) {
        viewModelScope.launch {
            repository.upsertGeofenceArea(id, name, lat, lon, radiusMeters)
        }
    }

    fun setCurrentLocation(latLng: LatLng) {
        _currentLocation.value = latLng
    }
}

