package com.example.travelcompanion.ui.statistics

import androidx.lifecycle.*
import com.example.travelcompanion.data.repository.TravelRepository
import com.example.travelcompanion.domain.model.TripStatistics
import kotlinx.coroutines.launch

class StatisticsViewModel(private val repository: TravelRepository) : ViewModel() {

    private val _statistics = MutableLiveData<TripStatistics>()
    val statistics: LiveData<TripStatistics> = _statistics

    init {
        loadStatistics()
    }

    fun loadStatistics() {
        viewModelScope.launch {
            val stats = repository.getTripStatistics()
            _statistics.value = stats
        }
    }
}

class StatisticsViewModelFactory(private val repository: TravelRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatisticsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StatisticsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}