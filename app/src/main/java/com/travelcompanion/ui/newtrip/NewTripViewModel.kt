package com.travelcompanion.ui.newtrip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelcompanion.domain.model.Trip
import com.travelcompanion.domain.model.TripType
import com.travelcompanion.domain.repository.ITripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class NewTripViewModel @Inject constructor(
    private val repository: ITripRepository
) : ViewModel() {

    private val _isTracking = MutableStateFlow(false)
    val isTracking: StateFlow<Boolean> = _isTracking.asStateFlow()

    private val _tripSaved = MutableStateFlow(false)
    val tripSaved: StateFlow<Boolean> = _tripSaved.asStateFlow()

    private val _createdTripId = MutableStateFlow(-1L)
    val createdTripId: StateFlow<Long> = _createdTripId.asStateFlow()

    private var currentTripId: Long = -1

    fun createTrip(
        title: String,
        destination: String,
        tripType: TripType,
        startDate: Date,
        endDate: Date,
        notes: String = ""
    ) {
        // Caller (NewTripFragment) already validates title and destination are non-blank.
        viewModelScope.launch {
            val trip = Trip(
                title = title,
                destination = destination,
                tripType = tripType,
                startDate = startDate,
                endDate = endDate,
                notes = notes
            )

            currentTripId = repository.insertTrip(trip)
            _tripSaved.value = true
            _createdTripId.value = currentTripId
        }
    }

    fun startTracking() {
        _isTracking.value = true
    }

    fun stopTracking() {
        _isTracking.value = false
    }

    fun resetSaveState() {
        _tripSaved.value = false
        _createdTripId.value = -1
    }

    override fun onCleared() {
        super.onCleared()
        Timber.d("NewTripViewModel cleared")
    }
}
