package com.travelcompanion.ui.statistics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.travelcompanion.domain.repository.ITripRepository
import com.travelcompanion.domain.model.MonthlyStat
import com.travelcompanion.domain.model.TripTypeStat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

/**
 * ViewModel for stats screen.
 * 
 * Loads trip analytics: total trips, distance, duration, monthly breakdown.
 * Supports filtering by time period: This Month, This Year, All Time.
 */
@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val repository: ITripRepository
) : ViewModel() {

    enum class TimePeriod {
        THIS_MONTH,
        THIS_YEAR,
        ALL_TIME
    }

    private var currentPeriod = TimePeriod.THIS_YEAR

    private val _totalTrips = MutableLiveData<Int>()
    val totalTrips: LiveData<Int> = _totalTrips

    private val _totalDistance = MutableLiveData<Float>()
    val totalDistance: LiveData<Float> = _totalDistance

    private val _totalDuration = MutableLiveData<Long>()
    val totalDuration: LiveData<Long> = _totalDuration

    private val _totalPhotos = MutableLiveData<Int>()
    val totalPhotos: LiveData<Int> = _totalPhotos

    private val _monthlyStats = MutableLiveData<List<MonthlyStat>>()
    val monthlyStats: LiveData<List<MonthlyStat>> = _monthlyStats

    private val _tripTypeStats = MutableLiveData<List<TripTypeStat>>()
    val tripTypeStats: LiveData<List<TripTypeStat>> = _tripTypeStats

    init {
        loadStatistics()
    }

    fun setTimePeriod(period: TimePeriod) {
        if (currentPeriod != period) {
            currentPeriod = period
            loadStatistics(forceReload = true)
        }
    }

    fun loadStatistics(forceReload: Boolean = false) {
        viewModelScope.launch {
            val allTrips = repository.getAllTrips().first()
            
            // Filter trips based on selected period
            val calendar = Calendar.getInstance()
            val currentYear = calendar.get(Calendar.YEAR)
            val currentMonth = calendar.get(Calendar.MONTH)
            
            val filteredTrips = when (currentPeriod) {
                TimePeriod.THIS_MONTH -> allTrips.filter { trip ->
                    val tripCal = Calendar.getInstance().apply { time = trip.startDate }
                    tripCal.get(Calendar.YEAR) == currentYear && 
                    tripCal.get(Calendar.MONTH) == currentMonth
                }
                TimePeriod.THIS_YEAR -> allTrips.filter { trip ->
                    val tripCal = Calendar.getInstance().apply { time = trip.startDate }
                    tripCal.get(Calendar.YEAR) == currentYear
                }
                TimePeriod.ALL_TIME -> allTrips
            }

            // Calculate stats from filtered trips
            _totalTrips.value = filteredTrips.size
            _totalDistance.value = filteredTrips.map { it.totalDistance }.sum()
            _totalPhotos.value = filteredTrips.sumOf { it.photoCount }
            
            // Calculate total duration (sum of all trip durations)
            var totalDurationMs = 0L
            filteredTrips.forEach { trip ->
                if (trip.endDate != null) {
                    totalDurationMs += trip.endDate.time - trip.startDate.time
                }
            }
            _totalDuration.value = totalDurationMs

            // Load monthly stats (only for the filtered year/period)
            val rawMonthly = repository.getMonthlyStats()
            val filteredMonthly = when (currentPeriod) {
                TimePeriod.THIS_MONTH -> rawMonthly.filter { it.month == currentMonth + 1 }
                TimePeriod.THIS_YEAR -> rawMonthly // Already filtered by current year in SQL
                TimePeriod.ALL_TIME -> rawMonthly
            }
            
            val byMonth = filteredMonthly.associateBy { it.month }
            val monthRange = when (currentPeriod) {
                TimePeriod.THIS_MONTH -> listOf(currentMonth + 1)
                else -> (1..12).toList()
            }
            
            _monthlyStats.value = monthRange.map { m ->
                val existing = byMonth[m]
                existing ?: MonthlyStat(
                    month = m,
                    tripCount = 0,
                    totalDistance = 0f,
                    totalDuration = 0L
                )
            }

            // Load trip type stats from filtered trips
            val typeStats = filteredTrips
                .groupBy { it.tripType }
                .map { (type, trips) -> 
                    TripTypeStat(
                        tripType = type,
                        totalDistance = trips.map { it.totalDistance }.sum(),
                        totalDuration = trips.sumOf { it.totalDuration },
                        tripCount = trips.size
                    )
                }
            _tripTypeStats.value = typeStats
        }
    }
}

