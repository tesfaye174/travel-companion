package com.travelcompanion.ui.newtrip

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelcompanion.domain.model.Trip
import com.travelcompanion.domain.model.TripType
import com.travelcompanion.domain.repository.ITripRepository
import com.travelcompanion.utils.GeocodingHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Date
import javax.inject.Inject

@HiltViewModel
@Suppress("unused")
class NewTripViewModel @Inject constructor(
    private val repository: ITripRepository,
    @param:ApplicationContext private val context: Context
) : ViewModel() {

    private val _isTracking = MutableLiveData(false)
    val isTracking: LiveData<Boolean> = _isTracking

    private val _tripSaved = MutableLiveData<Boolean>()
    val tripSaved: LiveData<Boolean> = _tripSaved

    private val _createdTripId = MutableLiveData<Long>()
    val createdTripId: LiveData<Long> = _createdTripId

    private var currentTripId: Long = -1

    fun createTrip(
        title: String,
        destination: String,
        tripType: TripType,
        startDate: Date,
        endDate: Date,
        notes: String = ""
    ) {
        viewModelScope.launch {
            // Geocode the destination to get coordinates
            val geocodingResult = GeocodingHelper.geocodeLocation(context, destination)

            if (geocodingResult != null) {
                Timber.d("Geocoded '$destination' to: ${geocodingResult.latitude}, ${geocodingResult.longitude}")
            } else {
                Timber.w("Could not geocode destination: $destination")
            }

            val trip = Trip(
                title = title,
                destination = destination,
                tripType = tripType,
                startDate = startDate,
                endDate = endDate,
                notes = notes,
                destinationLatitude = geocodingResult?.latitude,
                destinationLongitude = geocodingResult?.longitude
            )

            currentTripId = repository.insertTrip(trip)
            _tripSaved.value = true
            _createdTripId.value = currentTripId
        }
    }

    fun startTracking() {
        _isTracking.value = true
        // Here you would start the GPS tracking service
    }

    fun stopTracking() {
        _isTracking.value = false
        // Here you would stop the GPS tracking service
    }

    fun addPhoto() {
        // Implement photo capture
    }

    fun addNote() {
        // Implement note addition
    }

    fun resetSaveState() {
        _tripSaved.value = false
        _createdTripId.value = -1
    }
}
