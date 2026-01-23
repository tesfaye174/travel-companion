package com.travelcompanion.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.travelcompanion.domain.model.Trip
import com.travelcompanion.domain.repository.ITripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel per la schermata Home (dashboard).
 * 
 * Fornisce i dati per la schermata principale inclusi:
 * - Viaggi recenti (limitati a 3 per anteprima veloce)
 * - Riepilogo statistiche rapide (viaggi totali, distanza totale)
 * 
 * Progettato per caricamento veloce e visualizzazione istantanea
 * dei dati di viaggio più rilevanti a colpo d'occhio.
 * 
 * @property repository Il repository dei viaggi per le operazioni sui dati
 * 
 * @see HomeFragment
 * @see QuickStats
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ITripRepository
) : ViewModel() {

    // Viaggi recenti da mostrare nella schermata home
    val recentTrips: LiveData<List<Trip>> = repository.getAllTrips()
        .map { trips -> trips.take(3) } // Mostra solo gli ultimi 3 viaggi
        .asLiveData()

    private val _quickStats = MutableLiveData<QuickStats>()
    val quickStats: LiveData<QuickStats> = _quickStats

    init {
        loadQuickStats()
    }

    private fun loadQuickStats() {
        viewModelScope.launch {
            val tripCount = repository.getTripCount()
            val totalDistance = repository.getTotalDistance()
            _quickStats.value = QuickStats(tripCount, totalDistance)
        }
    }

    data class QuickStats(
        val totalTrips: Int,
        val totalDistance: Float
    )
}


