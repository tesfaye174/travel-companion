package com.example.travelcompanion.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelcompanion.domain.model.Trip
import com.example.travelcompanion.domain.repository.ITripRepository
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

    fun loadTripsForMap() {
        viewModelScope.launch {
            repository.getAllTrips().collect { tripsList ->
                _trips.value = tripsList
            }
        }
    }

    fun setCurrentLocation(latLng: LatLng) {
        _currentLocation.value = latLng
    }
}