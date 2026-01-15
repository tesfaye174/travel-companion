package com.example.travelcompanion.ui.trips

import androidx.lifecycle.*
import com.example.travelcompanion.domain.model.Trip
import com.example.travelcompanion.domain.repository.TravelRepository
import kotlinx.coroutines.launch

class TripViewModel(private val repository: TravelRepository) : ViewModel() {

    val allTrips: LiveData<List<Trip>> = repository.getAllTrips().asLiveData()

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
