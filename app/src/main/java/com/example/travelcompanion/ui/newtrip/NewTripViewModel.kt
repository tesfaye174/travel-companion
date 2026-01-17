package com.example.travelcompanion.ui.newtrip

import androidx.lifecycle.*
import com.example.travelcompanion.data.repository.TravelRepository
import com.example.travelcompanion.domain.model.Trip
import com.example.travelcompanion.domain.model.TripType
import kotlinx.coroutines.launch

class NewTripViewModel(private val repository: TravelRepository) : ViewModel() {

    private val _destination = MutableLiveData("")
    val destination: LiveData<String> = _destination

    private val _tripType = MutableLiveData(TripType.MULTI_DAY)
    val tripType: LiveData<TripType> = _tripType

    private val _startDate = MutableLiveData<Long>()
    val startDate: LiveData<Long> = _startDate

    private val _endDate = MutableLiveData<Long>()
    val endDate: LiveData<Long> = _endDate

    private val _notes = MutableLiveData("")
    val notes: LiveData<String> = _notes

    private val _tripCreated = MutableLiveData<Long?>()
    val tripCreated: LiveData<Long?> = _tripCreated

    fun setDestination(value: String) {
        _destination.value = value
    }

    fun setTripType(type: TripType) {
        _tripType.value = type
    }

    fun setStartDate(date: Long) {
        _startDate.value = date
    }

    fun setEndDate(date: Long) {
        _endDate.value = date
    }

    fun setNotes(value: String) {
        _notes.value = value
    }

    fun createTrip(title: String) {
        viewModelScope.launch {
            val trip = Trip(
                title = title,
                destination = _destination.value ?: "",
                tripType = _tripType.value ?: TripType.MULTI_DAY,
                startDate = _startDate.value ?: System.currentTimeMillis(),
                endDate = _endDate.value ?: System.currentTimeMillis(),
                notes = _notes.value
            )
            val tripId = repository.insertTrip(trip)
            _tripCreated.value = tripId
        }
    }
}

class NewTripViewModelFactory(private val repository: TravelRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewTripViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NewTripViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}