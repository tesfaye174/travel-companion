package com.travelcompanion.domain.model

import org.junit.Assert.assertThrows
import org.junit.Test
import java.util.Date

class TripValidationUtilsTest {

    @Test
    fun `validateForCreate succeeds with valid trip`() {
        val trip = createTrip()
        TripValidationUtils.validateForCreate(trip)
    }

    @Test
    fun `validateForCreate fails with blank title`() {
        val trip = createTrip(title = "")
        assertThrows(IllegalArgumentException::class.java) {
            TripValidationUtils.validateForCreate(trip)
        }
    }

    @Test
    fun `validateForCreate fails with blank destination`() {
        val trip = createTrip(destination = "   ")
        assertThrows(IllegalArgumentException::class.java) {
            TripValidationUtils.validateForCreate(trip)
        }
    }

    @Test
    fun `validateForCreate fails when endDate before startDate`() {
        val start = Date(2000000000L)
        val end = Date(1000000000L)
        val trip = createTrip(startDate = start, endDate = end)
        assertThrows(IllegalArgumentException::class.java) {
            TripValidationUtils.validateForCreate(trip)
        }
    }

    @Test
    fun `validateForCreate succeeds with null endDate`() {
        val trip = createTrip(endDate = null)
        TripValidationUtils.validateForCreate(trip)
    }

    @Test
    fun `validateForUpdate fails with zero id`() {
        val trip = createTrip(id = 0)
        assertThrows(IllegalArgumentException::class.java) {
            TripValidationUtils.validateForUpdate(trip)
        }
    }

    @Test
    fun `validateForUpdate succeeds with valid trip`() {
        val trip = createTrip(id = 5)
        TripValidationUtils.validateForUpdate(trip)
    }

    @Test
    fun `validateForDelete fails with zero id`() {
        val trip = createTrip(id = 0)
        assertThrows(IllegalArgumentException::class.java) {
            TripValidationUtils.validateForDelete(trip)
        }
    }

    @Test
    fun `validateForDelete succeeds with valid id`() {
        val trip = createTrip(id = 1)
        TripValidationUtils.validateForDelete(trip)
    }

    private fun createTrip(
        id: Long = 1,
        title: String = "Test Trip",
        destination: String = "Rome, Italy",
        startDate: Date = Date(),
        endDate: Date? = Date()
    ) = Trip(
        id = id,
        title = title,
        destination = destination,
        tripType = TripType.LOCAL,
        startDate = startDate,
        endDate = endDate
    )
}
