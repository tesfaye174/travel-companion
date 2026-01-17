package com.example.travelcompanion

import android.app.Application
import com.example.travelcompanion.data.local.TravelDatabase
import com.example.travelcompanion.data.repository.TravelRepository

class TravelCompanionApplication : Application() {

    private val database by lazy { TravelDatabase.getDatabase(this) }

    val repository by lazy {
        TravelRepository(
            database.tripDao(),
            database.journeyDao(),
            database.locationPointDao(),
            database.photoDao(),
            database.noteDao()
        )
    }
}