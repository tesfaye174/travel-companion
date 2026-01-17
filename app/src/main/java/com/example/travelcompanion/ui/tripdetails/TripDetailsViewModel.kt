package com.example.travelcompanion.ui.tripdetails

import androidx.lifecycle.*
import com.example.travelcompanion.data.repository.TravelRepository
import com.example.travelcompanion.domain.model.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TripDetailsViewModel(
    private val repository: TravelRepository,
    private val tripId: Long
) : ViewModel() {

    val tripDetails: StateFlow<TripDetails?> = repository.getTripDetails(tripId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val photos: StateFlow<List<Photo>> = repository.getPhotosForTrip(tripId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notes: StateFlow<List<Note>> = repository.getNotesForTrip(tripId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun startTracking() {
        viewModelScope.launch {
            repository.setActiveTrip(tripId)
            repository.startJourney(tripId)
        }
    }

    fun addNote(title: String, content: String, latitude: Double? = null, longitude: Double? = null) {
        viewModelScope.launch {
            val note = Note(
                tripId = tripId,
                title = title,
                content = content,
                latitude = latitude,
                longitude = longitude
            )
            repository.insertNote(note)
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }
}

class TripDetailsViewModelFactory(
    private val repository: TravelRepository,
    private val tripId: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TripDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TripDetailsViewModel(repository, tripId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}