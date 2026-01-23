package com.travelcompanion.ui.tripdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.travelcompanion.domain.model.Note
import com.travelcompanion.domain.model.PhotoNote
import com.travelcompanion.domain.model.Trip
import com.travelcompanion.domain.repository.ITripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

/**
 * ViewModel per la schermata Dettagli Viaggio.
 * 
 * Gestisce ed espone tutti i dati relativi a un singolo viaggio inclusi:
 * - Informazioni del viaggio (titolo, destinazione, date, tipo)
 * - Dati del percorso con coordinate GPS
 * - Foto con note e geolocalizzazione
 * - Note testuali allegate al viaggio
 * 
 * Supporta operazioni CRUD per la modifica del viaggio, aggiunta foto,
 * e allegare note a momenti o luoghi specifici.
 * 
 * @property repository Il repository dei viaggi per le operazioni sui dati
 * 
 * @see TripDetailsFragment
 * @see Trip
 * @see PhotoNote
 * @see Note
 */
@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class TripDetailsViewModel @Inject constructor(
    private val repository: ITripRepository
) : ViewModel() {

    private val tripIdFlow = MutableStateFlow(-1L)

    fun setTripId(id: Long) {
        tripIdFlow.value = id
    }

    val trip = tripIdFlow
        .filter { it > 0 }
        .flatMapLatest { id -> flow { emit(repository.getTripById(id)) } }
        .asLiveData()

    val journeys = tripIdFlow
        .filter { it > 0 }
        .flatMapLatest { id -> repository.getJourneysByTripId(id) }
        .asLiveData()

    val photoNotes = tripIdFlow
        .filter { it > 0 }
        .flatMapLatest { id -> repository.getPhotoNotesByTripId(id) }
        .asLiveData()

    val notes = tripIdFlow
        .filter { it > 0 }
        .flatMapLatest { id -> repository.getNotesByTripId(id) }
        .asLiveData()

    val totalDistanceKm = tripIdFlow
        .filter { it > 0 }
        .flatMapLatest { id -> repository.getJourneysByTripId(id) }
        .map { list ->
            // Summing up distances from the list to calculate the total distance.
            list.sumOf { it.distance.toDouble() } }
        .asLiveData()

    val totalDurationMs = tripIdFlow
        .filter { it > 0 }
        .flatMapLatest { id -> repository.getJourneysByTripId(id) }
        .map { list ->
            list.sumOf { j ->
                val end = j.endTime?.time ?: return@sumOf 0L
                val start = j.startTime.time
                (end - start).coerceAtLeast(0L)
            }
        }
        .asLiveData()

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
            
            // Update photo count on trip
            val currentTrip = repository.getTripById(tripId)
            currentTrip?.let {
                repository.updateTrip(it.copy(photoCount = it.photoCount + 1))
            }
        }
    }
}

