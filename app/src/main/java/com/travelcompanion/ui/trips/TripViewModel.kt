package com.travelcompanion.ui.trips

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.travelcompanion.domain.model.Trip
import com.travelcompanion.domain.model.TripType
import com.travelcompanion.domain.usecase.CreateTripUseCase
import com.travelcompanion.domain.usecase.DeleteTripUseCase
import com.travelcompanion.domain.usecase.GetAllTripsUseCase
import com.travelcompanion.domain.usecase.GetTripByIdUseCase
import com.travelcompanion.domain.usecase.GetTripsByTypeUseCase
import com.travelcompanion.domain.usecase.UpdateTripUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for trips list screen.
 * Handles filtering, search, and CRUD operations.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TripViewModel @Inject constructor(
    private val getAllTripsUseCase: GetAllTripsUseCase,
    private val getTripByIdUseCase: GetTripByIdUseCase,
    private val updateTripUseCase: UpdateTripUseCase,
    private val deleteTripUseCase: DeleteTripUseCase,
    private val createTripUseCase: CreateTripUseCase,
    private val getTripsByTypeUseCase: GetTripsByTypeUseCase
) : ViewModel() {

    // Using Flow and transforming to LiveData for UI
    private val _filterType = MutableStateFlow<TripType?>(null)

    private val _searchQuery = MutableStateFlow("")
    
    private val baseTripsFlow = _filterType.flatMapLatest { type ->
        if (type == null) getAllTripsUseCase() else getTripsByTypeUseCase(type)
    }

    val allTrips: LiveData<List<Trip>> = baseTripsFlow
        .combine(_searchQuery) { trips, query ->
            val q = query.trim()
            if (q.isBlank()) return@combine trips
            trips.filter {
                it.title.contains(q, ignoreCase = true) ||
                    it.destination.contains(q, ignoreCase = true) ||
                    it.notes.contains(q, ignoreCase = true)
            }
        }
        .asLiveData()

    fun setFilterType(type: TripType?) {
        _filterType.value = type
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun insertTrip(trip: Trip) {
        viewModelScope.launch {
            try {
                createTripUseCase(trip)
            } catch (e: Exception) {
                // handle error
            }
        }
    }

    fun updateTrip(trip: Trip) {
        viewModelScope.launch {
            try {
                updateTripUseCase(trip)
            } catch (e: Exception) {
                // handle error
            }
        }
    }

    fun deleteTrip(trip: Trip) {
        viewModelScope.launch {
            try {
                deleteTripUseCase(trip)
            } catch (e: Exception) {
                // handle error
            }
        }
    }

    fun getTripById(id: Long): Flow<Trip?> {
        return getTripByIdUseCase(id)
    }
}

