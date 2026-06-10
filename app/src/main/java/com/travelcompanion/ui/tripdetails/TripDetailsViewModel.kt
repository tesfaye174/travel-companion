package com.travelcompanion.ui.tripdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelcompanion.domain.model.Journey
import com.travelcompanion.domain.model.Note
import com.travelcompanion.domain.model.PhotoNote
import com.travelcompanion.domain.model.Trip
import com.travelcompanion.domain.repository.ITripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class TripDetailsViewModel @Inject constructor(
    private val repository: ITripRepository
) : ViewModel() {

    private val tripIdFlow = MutableStateFlow(-1L)

    fun setTripId(id: Long) {
        tripIdFlow.value = id
    }

    val trip: StateFlow<Trip?> = tripIdFlow
        .filter { it > 0 }
        .flatMapLatest { id -> repository.getTripByIdFlow(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val journeys: StateFlow<List<Journey>> = tripIdFlow
        .filter { it > 0 }
        .flatMapLatest { id -> repository.getJourneysByTripId(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val photoNotes: StateFlow<List<PhotoNote>> = tripIdFlow
        .filter { it > 0 }
        .flatMapLatest { id -> repository.getPhotoNotesByTripId(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notes: StateFlow<List<Note>> = tripIdFlow
        .filter { it > 0 }
        .flatMapLatest { id -> repository.getNotesByTripId(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Distanza calcolata dai Journey (in metri). Fallback alla Trip.totalDistance (in km)
    // se non ci sono ancora punti tracciati
    val totalDistanceKm: StateFlow<Double> = combine(journeys, trip) { js, t ->
        val fromJourneys = js.sumOf { it.distance.toDouble() } / 1000.0
        if (fromJourneys > 0.0) fromJourneys else (t?.totalDistance?.toDouble() ?: 0.0)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalDurationMs: StateFlow<Long> = combine(journeys, trip) { js, t ->
        val fromJourneys = js.sumOf { j ->
            val end = j.endTime?.time ?: return@sumOf 0L
            val start = j.startTime.time
            (end - start).coerceAtLeast(0L)
        }
        if (fromJourneys > 0L) fromJourneys else (t?.totalDuration ?: 0L)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    fun updateTrip(trip: Trip) {
        viewModelScope.launch {
            repository.updateTrip(trip)
        }
    }

    fun addNote(content: String) {
        val tripId = tripIdFlow.value
        if (tripId <= 0) return

        viewModelScope.launch {
            val note = Note(
                tripId = tripId,
                content = content,
                timestamp = Date()
            )
            repository.insertNote(note)
        }
    }

    fun addPhotoNote(imagePath: String, note: String, latitude: Double?, longitude: Double?) {
        val tripId = tripIdFlow.value
        if (tripId <= 0) return

        viewModelScope.launch {
            val photoNote = PhotoNote(
                tripId = tripId,
                imagePath = imagePath,
                note = note,
                latitude = latitude,
                longitude = longitude,
                timestamp = Date()
            )
            repository.insertPhotoNote(photoNote)

            val currentTrip = repository.getTripById(tripId)
            currentTrip?.let {
                repository.updateTrip(it.copy(photoCount = it.photoCount + 1))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Timber.d("TripDetailsViewModel cleared")
    }
}
