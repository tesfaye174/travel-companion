package com.travelcompanion.ui.newtrip

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelcompanion.domain.model.Trip
import com.travelcompanion.domain.model.TripType
import com.travelcompanion.domain.usecase.CreateTripUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class NewTripViewModel @Inject constructor(
    private val createTripUseCase: CreateTripUseCase
) : ViewModel() {

    private val _isTracking = MutableLiveData(false)
    val isTracking: LiveData<Boolean> = _isTracking

    private val _tripSaved = MutableLiveData<Boolean>()
    val tripSaved: LiveData<Boolean> = _tripSaved

    private val _createdTripId = MutableLiveData<Long>()
    val createdTripId: LiveData<Long> = _createdTripId

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private var currentTripId: Long = -1

    fun createTrip(
        title: String,
        destination: String,
        tripType: TripType,
        startDate: Date,
        endDate: Date,
        notes: String = ""
    ) {
        viewModelScope.launch {
            val trip = Trip(
                title = title,
                destination = destination,
                tripType = tripType,
                startDate = startDate,
                endDate = endDate,
                notes = notes
            )

            try {
                currentTripId = createTripUseCase(trip)
                _tripSaved.value = true
                _createdTripId.value = currentTripId
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to create trip"
                _tripSaved.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun startTracking() {
        _isTracking.value = true
        // Here you would start the GPS tracking service
    }

    fun stopTracking() {
        _isTracking.value = false
        // Here you would stop the GPS tracking service
    }

    fun addPhoto() {
        // Implement photo capture
    }

    fun addNote() {
        // Implement note addition
    }

    fun resetSaveState() {
        _tripSaved.value = false
        _createdTripId.value = -1
    }
}

