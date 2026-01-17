package com.example.travelcompanion.ui.trips

import androidx.lifecycle.*
import com.example.travelcompanion.data.repository.TravelRepository
import com.example.travelcompanion.domain.model.Trip
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TripsViewModel(private val repository: TravelRepository) : ViewModel() {

    private val _selectedFilter = MutableLiveData(TripFilter.ALL)
    val selectedFilter: LiveData<TripFilter> = _selectedFilter

    val trips: StateFlow<List<Trip>> = repository.getAllTrips()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val upcomingTrips: StateFlow<List<Trip>> = repository.getUpcomingTrips()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pastTrips: StateFlow<List<Trip>> = repository.getPastTrips()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setFilter(filter: TripFilter) {
        _selectedFilter.value = filter
    }

    fun deleteTrip(trip: Trip) {
        viewModelScope.launch {
            repository.deleteTrip(trip)
        }
    }
}

enum class TripFilter {
    ALL, UPCOMING, PAST, LOCAL, MULTI_DAY
}

class TripsViewModelFactory(private val repository: TravelRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TripsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TripsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}