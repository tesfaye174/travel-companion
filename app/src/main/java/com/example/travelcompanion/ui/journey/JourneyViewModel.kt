package com.example.travelcompanion.ui.journey

import androidx.lifecycle.*
import com.example.travelcompanion.domain.model.*
import com.example.travelcompanion.domain.repository.TravelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class JourneyViewModel(private val repository: TravelRepository) : ViewModel() {

    private val _currentJourney = MutableStateFlow<Journey?>(null)
    val currentJourney: StateFlow<Journey?> = _currentJourney

    private val _activeTripId = MutableStateFlow<Long>(-1L)
    val activeTripId: StateFlow<Long> = _activeTripId

    fun startJourney(tripId: Long) = viewModelScope.launch {
        val journey = Journey(tripId = tripId, startTime = System.currentTimeMillis(), endTime = null)
        val id = repository.insertJourney(journey)
        _currentJourney.value = journey.copy(id = id)
        _activeTripId.value = tripId
    }

    fun stopJourney() = viewModelScope.launch {
        _currentJourney.value?.let { journey ->
            val updated = journey.copy(endTime = System.currentTimeMillis())
            // Update in DB (need to add updateJourney to repo)
            repository.updateJourney(updated)
            _currentJourney.value = null
            _activeTripId.value = -1L
        }
    }

    fun getPointsForJourney(journeyId: Long): LiveData<List<Point>> {
        return repository.getPointsByJourney(journeyId).asLiveData()
    }

    fun insertPhoto(photo: Photo) = viewModelScope.launch {
        repository.insertPhoto(photo)
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
