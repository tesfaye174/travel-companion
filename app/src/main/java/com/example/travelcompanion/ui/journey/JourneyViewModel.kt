package com.example.travelcompanion.ui.journey

import androidx.lifecycle.*
import com.example.travelcompanion.data.repository.TravelRepository
import com.example.travelcompanion.domain.model.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class JourneyViewModel(private val repository: TravelRepository) : ViewModel() {

    val currentJourney: StateFlow<Journey?> = repository.getActiveJourney()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val activeTrip: StateFlow<Trip?> = repository.getActiveTrip()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun getPointsForJourney(journeyId: Long): LiveData<List<LocationPoint>> =
        repository.getPointsForJourney(journeyId)

    fun startJourney(tripId: Long) {
        viewModelScope.launch {
            repository.startJourney(tripId)
        }
    }

    fun stopJourney(journeyId: Long) {
        viewModelScope.launch {
            repository.stopJourney(journeyId)
        }
    }

    fun insertLocationPoint(point: LocationPoint) {
        viewModelScope.launch {
            repository.insertLocationPoint(point)
        }
    }

    fun insertPhoto(photo: Photo) {
        viewModelScope.launch {
            repository.insertPhoto(photo)
        }
    }
}

class JourneyViewModelFactory(private val repository: TravelRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(JourneyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return JourneyViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}