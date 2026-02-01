package com.travelcompanion.ui.tracking

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.travelcompanion.domain.model.Trip
import com.travelcompanion.domain.repository.ITripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * ViewModel per la schermata di tracking GPS.
 *
 * Gestisce tutto lo stato relativo al tracking in tempo reale:
 * - Stato del tracking (idle, tracking, stopped)
 * - Tempo trascorso (aggiornato ogni secondo)
 * - Distanza percorsa (calcolata dai punti GPS)
 * - Velocità attuale
 * - Conteggio foto scattate
 * - Storico posizioni per disegnare la polyline sulla mappa
 *
 * Lo stato è gestito con StateFlow invece di LiveData perché:
 * - È più efficiente per aggiornamenti frequenti (ogni secondo)
 * - Si integra meglio con Jetpack Compose (se decidessi di migrare)
 * - Supporta operatori Flow come combine, flatMapLatest ecc.
 *
 * @param repository Per salvare i dati del percorso quando il tracking termina
 */
@HiltViewModel
class TrackingViewModel @Inject constructor(
    private val repository: ITripRepository
) : ViewModel() {

    // ==================== STATO DEL TRACKING ====================

    /**
     * Sealed class che rappresenta i possibili stati del tracking.
     * Uso una sealed class invece di un enum per poter passare dati
     * nello stato Tracking (l'ID del viaggio corrente).
     */
    sealed class TrackingState {
        object Idle : TrackingState()              // Tracking non avviato
        data class Tracking(val tripId: Long) : TrackingState()  // In corso
        object Stopped : TrackingState()           // Terminato
    }

    private val _trackingState = MutableStateFlow<TrackingState>(TrackingState.Idle)
    val trackingState: StateFlow<TrackingState> = _trackingState.asStateFlow()

    // Trip data
    private val _currentTrip = MutableLiveData<Trip?>()
    val currentTrip: LiveData<Trip?> = _currentTrip

    // Elapsed time in seconds
    private val _elapsedTimeSeconds = MutableStateFlow(0L)
    val elapsedTimeSeconds: StateFlow<Long> = _elapsedTimeSeconds.asStateFlow()

    // Distance in meters
    private val _distanceMeters = MutableStateFlow(0f)
    val distanceMeters: StateFlow<Float> = _distanceMeters.asStateFlow()

    // Current speed in m/s
    private val _currentSpeedMps = MutableStateFlow(0f)
    val currentSpeedMps: StateFlow<Float> = _currentSpeedMps.asStateFlow()

    // Photo count
    private val _photoCount = MutableStateFlow(0)
    val photoCount: StateFlow<Int> = _photoCount.asStateFlow()

    // Current location
    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> = _currentLocation.asStateFlow()

    // Location history for polyline
    private val _locationHistory = MutableStateFlow<List<Location>>(emptyList())
    val locationHistory: StateFlow<List<Location>> = _locationHistory.asStateFlow()

    // Timer job
    private var timerJob: Job? = null
    private var startTime: Long = 0L

    // Previous location for distance calculation
    private var previousLocation: Location? = null

    /**
     * Start tracking for a given trip
     */
    fun startTracking(tripId: Long) {
        if (_trackingState.value is TrackingState.Tracking) return

        viewModelScope.launch {
            val trip = repository.getTripById(tripId)
            _currentTrip.value = trip
            _trackingState.value = TrackingState.Tracking(tripId)
            _photoCount.value = trip?.photoCount ?: 0

            // Reset tracking data
            _distanceMeters.value = 0f
            _currentSpeedMps.value = 0f
            _locationHistory.value = emptyList()
            previousLocation = null

            // Start timer
            startTimer()
        }
    }

    /**
     * Ferma il tracking corrente.
     * Il salvataggio del Journey viene gestito dal TrackingService.
     */
    fun stopTracking() {
        timerJob?.cancel()
        timerJob = null
        _trackingState.value = TrackingState.Stopped
    }

    /**
     * Chiamato dal TrackingService quando arriva una nuova posizione GPS.
     *
     * Calcola la distanza incrementale dal punto precedente e aggiorna
     * lo storico posizioni per la polyline sulla mappa.
     *
     * Ignoro movimenti < 1 metro per filtrare il rumore del GPS quando
     * l'utente è fermo (il GPS può oscillare anche di qualche metro).
     */
    fun onLocationUpdate(location: Location) {
        _currentLocation.value = location
        _currentSpeedMps.value = location.speed

        // Calculate distance from previous point
        previousLocation?.let { prev ->
            val distance = prev.distanceTo(location)
            if (distance > 1f) { // Soglia minima per filtrare rumore GPS
                _distanceMeters.value += distance
            }
        }
        previousLocation = location

        // Aggiungo la posizione allo storico per disegnare la polyline
        _locationHistory.value = _locationHistory.value + location
    }

    /**
     * Incrementa il contatore foto quando l'utente scatta una foto.
     */
    fun incrementPhotoCount() {
        _photoCount.value++
    }

    // ==================== METODI DI FORMATTAZIONE ====================
    // Questi metodi formattano i dati grezzi per la visualizzazione nella UI.
    // Ho scelto di metterli qui invece che nella View per testabilità.

    /**
     * Formatta il tempo trascorso in formato HH:mm:ss.
     * Es: 01:23:45 per 1 ora, 23 minuti e 45 secondi
     */
    fun getFormattedElapsedTime(): String {
        val seconds = _elapsedTimeSeconds.value
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, secs)
    }

    /**
     * Get formatted distance (km)
     */
    fun getFormattedDistance(): String {
        val km = _distanceMeters.value / 1000f
        return String.format(Locale.getDefault(), "%.1f", km)
    }

    /**
     * Get formatted speed (km/h)
     */
    fun getFormattedSpeed(): String {
        val kmh = _currentSpeedMps.value * 3.6f
        return String.format(Locale.getDefault(), "%.0f", kmh)
    }

    /**
     * Avvia il timer che aggiorna il tempo trascorso ogni secondo.
     *
     * Uso un loop infinito con delay invece di un Timer perché:
     * - Si integra meglio con le coroutine e viewModelScope
     * - Viene cancellato automaticamente quando il ViewModel viene distrutto
     * - Non devo preoccuparmi di memory leak
     */
    private fun startTimer() {
        timerJob?.cancel()  // Cancello eventuali timer precedenti
        startTime = System.currentTimeMillis()
        timerJob = viewModelScope.launch {
            while (true) {
                val elapsed = (System.currentTimeMillis() - startTime) / 1000
                _elapsedTimeSeconds.value = elapsed
                delay(1000)  // Aspetto 1 secondo prima del prossimo aggiornamento
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
