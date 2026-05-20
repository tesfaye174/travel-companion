package com.travelcompanion.ui.trips

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelcompanion.domain.model.Trip
import com.travelcompanion.domain.model.TripType
import com.travelcompanion.domain.repository.ITripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SortOrder { DATE_DESC, DATE_ASC, DISTANCE, NAME }

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TripViewModel @Inject constructor(
    private val repository: ITripRepository
) : ViewModel() {

    private val _filterType = MutableStateFlow<TripType?>(null)
    private val _searchQuery = MutableStateFlow("")
    private val _sortOrder = MutableStateFlow(SortOrder.DATE_DESC)

    private val baseTripsFlow = _filterType.flatMapLatest { type ->
        if (type == null) repository.getAllTrips() else repository.getTripsByType(type)
    }

    val allTrips: StateFlow<List<Trip>> = baseTripsFlow
        .combine(_searchQuery) { trips, query ->
            val q = query.trim()
            if (q.isBlank()) trips
            else trips.filter {
                it.title.contains(q, ignoreCase = true) ||
                    it.destination.contains(q, ignoreCase = true) ||
                    it.notes.contains(q, ignoreCase = true)
            }
        }
        .combine(_sortOrder) { trips, order ->
            when (order) {
                SortOrder.DATE_DESC -> trips.sortedByDescending { it.startDate }
                SortOrder.DATE_ASC -> trips.sortedBy { it.startDate }
                SortOrder.DISTANCE -> trips.sortedByDescending { it.totalDistance }
                SortOrder.NAME -> trips.sortedBy { it.title.lowercase() }
            }
        }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setFilterType(type: TripType?) {
        _filterType.value = type
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSortOrder(order: SortOrder) {
        _sortOrder.value = order
    }

    fun insertTrip(trip: Trip) {
        viewModelScope.launch {
            repository.insertTrip(trip)
        }
    }

    fun updateTrip(trip: Trip) {
        viewModelScope.launch {
            repository.updateTrip(trip)
        }
    }

    fun deleteTrip(trip: Trip) {
        viewModelScope.launch {
            repository.deleteTrip(trip)
        }
    }

    suspend fun getTripById(id: Long): Trip? {
        return repository.getTripById(id)
    }

    override fun onCleared() {
        super.onCleared()
        Timber.d("TripViewModel cleared")
    }
}
