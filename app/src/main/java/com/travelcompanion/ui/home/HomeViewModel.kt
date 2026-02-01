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
 * ViewModel per la schermata Home (dashboard) dell'app.
 *
 * Questa schermata mostra:
 * - I viaggi recenti (ultimi 3) per accesso rapido
 * - Statistiche veloci (numero viaggi e km totali)
 * - Destinazioni suggerite per ispirare nuovi viaggi
 *
 * Ho scelto di limitare a 3 i viaggi recenti per non appesantire
 * la home. L'utente può vedere tutti i viaggi nella sezione dedicata.
 *
 * @param repository Iniettato da Hilt, fornisce accesso ai dati
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ITripRepository
) : ViewModel() {

    // Flow dei viaggi trasformato per prendere solo i primi 3.
    // asLiveData() converte il Flow in LiveData per l'osservazione dalla UI.
    val recentTrips: LiveData<List<Trip>> = repository.getAllTrips()
        .map { trips -> trips.take(3) }
        .asLiveData()

    // Le statistiche veloci vengono caricate una volta all'inizializzazione
    private val _quickStats = MutableLiveData<QuickStats>()
    val quickStats: LiveData<QuickStats> = _quickStats

    init {
        // Carico le statistiche quando il ViewModel viene creato
        loadQuickStats()
    }

    /**
     * Carica le statistiche aggregate dal repository.
     * Uso viewModelScope per gestire automaticamente la cancellazione
     * della coroutine quando il ViewModel viene distrutto.
     */
    private fun loadQuickStats() {
        viewModelScope.launch {
            val tripCount = repository.getTripCount()
            val totalDistance = repository.getTotalDistance()
            _quickStats.value = QuickStats(tripCount, totalDistance)
        }
    }

    /**
     * Data class per le statistiche mostrate nella home.
     * Uso una data class interna perché queste statistiche sono specifiche
     * di questo ViewModel e non hanno senso altrove.
     */
    data class QuickStats(
        val totalTrips: Int,
        val totalDistance: Float   // Distanza totale in km
    )
}


