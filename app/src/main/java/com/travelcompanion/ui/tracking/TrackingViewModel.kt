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

// ViewModel for GPS tracking - handles tracking state, elapsed time, distance, speed
@HiltViewModel
class TrackingViewModel @Inject constructor(
    private val repository: ITripRepository
) : ViewModel() {

    // Tracking state
    sealed class TrackingState {
        object Idle : TrackingState()
        data class Tracking(val tripId: Long) : TrackingState()
        object Stopped : TrackingState()
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
     * Stop tracking
     */
    fun stopTracking() {
        timerJob?.cancel()
        timerJob = null
        _trackingState.value = TrackingState.Stopped
    }

    /**
     * Update location from service
     */
    fun onLocationUpdate(location: Location) {
        _currentLocation.value = location
        _currentSpeedMps.value = location.speed

        // Calculate distance from previous point
        previousLocation?.let { prev ->
            val distance = prev.distanceTo(location)
            if (distance > 1f) { // Ignore noise < 1 meter
                _distanceMeters.value += distance
            }
        }
        previousLocation = location

        // Add to history for polyline
        _locationHistory.value = _locationHistory.value + location
    }

    /**
     * Update photo count when a new photo is taken
     */
    fun incrementPhotoCount() {
        _photoCount.value++
    }

    /**
     * Get formatted elapsed time (HH:mm:ss)
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

    private fun startTimer() {
        timerJob?.cancel()
        startTime = System.currentTimeMillis()
        timerJob = viewModelScope.launch {
            while (true) {
                val elapsed = (System.currentTimeMillis() - startTime) / 1000
                _elapsedTimeSeconds.value = elapsed
                delay(1000)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
