package com.travelcompanion.ui.trips

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.travelcompanion.domain.model.Trip
import com.travelcompanion.domain.model.TripType
import com.travelcompanion.domain.repository.ITripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsabile della gestione dello stato della schermata lista viaggi.
 * 
 * Questo ViewModel fornisce funzionalità per:
 * - Caricamento e visualizzazione di tutti i viaggi
 * - Filtro viaggi per tipo (auto, aereo, treno, ecc.)
 * - Ricerca viaggi per titolo, destinazione o note
 * - Operazioni CRUD sui viaggi
 * 
 * Utilizza Kotlin Flow per il data binding reattivo con aggiornamenti
 * automatici della UI quando i dati sottostanti cambiano.
 * 
 * @property repository Il repository dei viaggi per le operazioni sui dati
 * 
 * @see ITripRepository
 * @see TripsFragment
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TripViewModel @Inject constructor(
    private val repository: ITripRepository
) : ViewModel() {

    // Using Flow and transforming to LiveData for UI
    private val _filterType = MutableStateFlow<TripType?>(null)

    private val _searchQuery = MutableStateFlow("")
    
    private val baseTripsFlow = _filterType.flatMapLatest { type ->
        if (type == null) repository.getAllTrips() else repository.getTripsByType(type)
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
}

