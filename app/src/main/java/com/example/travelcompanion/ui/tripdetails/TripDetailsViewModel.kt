package com.example.travelcompanion.ui.tripdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelcompanion.domain.model.Trip
import com.example.travelcompanion.domain.repository.ITripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TripDetailViewModel @Inject constructor(
    private val repository: ITripRepository
) : ViewModel() {

    private val _trip = MutableLiveData<Trip?>()
    val trip: LiveData<Trip?> = _trip

    private val _deleteSuccess = MutableLiveData<Boolean>()
    val deleteSuccess: LiveData<Boolean> = _deleteSuccess

    fun loadTrip(tripId: Long) {
        viewModelScope.launch {
            val loadedTrip = repository.getTripById(tripId)
            _trip.value = loadedTrip
        }
    }

    fun deleteTrip() {
        viewModelScope.launch {
            _trip.value?.let { trip ->
                repository.deleteTrip(trip)
                _deleteSuccess.value = true
            }
        }
    }

    fun resetDeleteState() {
        _deleteSuccess.value = false
    }
}