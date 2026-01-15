package com.example.travelcompanion.data.db

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.travelcompanion.data.db.dao.TripDao
import com.example.travelcompanion.data.db.entities.TripEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TripDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var tripDao: TripDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        tripDao = database.tripDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndGetTrip() = runBlocking {
        val trip = TripEntity(destination = "Paris", startDate = 123L, endDate = null, type = "Day")
        tripDao.insertTrip(trip)
        
        val allTrips = tripDao.getAllTrips().first()
        assertEquals(1, allTrips.size)
        assertEquals("Paris", allTrips[0].destination)
    }
}
