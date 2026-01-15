package com.example.travelcompanion.ui.trips

import androidx.lifecycle.*
import com.example.travelcompanion.domain.model.Trip
import com.example.travelcompanion.domain.model.TripType
import com.example.travelcompanion.domain.repository.TravelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class TripViewModel(private val repository: TravelRepository) : ViewModel() {

    private val _filterDestination = MutableStateFlow("")
    val filterDestination: StateFlow<String> = _filterDestination

    private val _filterType = MutableStateFlow<TripType?>(null)
    val filterType: StateFlow<TripType?> = _filterType

    val allTrips: LiveData<List<Trip>> = combine(
        repository.getAllTrips(),
        _filterDestination,
        _filterType
    ) { trips, destFilter, typeFilter ->
        trips.filter { trip ->
            (destFilter.isEmpty() || trip.destination.contains(destFilter, ignoreCase = true)) &&
            (typeFilter == null || trip.type == typeFilter)
        }
    }.asLiveData()

    fun setFilterDestination(filter: String) {
        _filterDestination.value = filter
    }

    fun setFilterType(type: TripType?) {
        _filterType.value = type
    }

    fun insertTrip(trip: Trip) = viewModelScope.launch {
        repository.insertTrip(trip)
    }

    fun deleteTrip(trip: Trip) = viewModelScope.launch {
        repository.deleteTrip(trip)
    }
}

class TripViewModelFactory(private val repository: TravelRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TripViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TripViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
