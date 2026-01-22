package com.travelcompanion.ui.tripdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.travelcompanion.domain.repository.ITripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
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
}

