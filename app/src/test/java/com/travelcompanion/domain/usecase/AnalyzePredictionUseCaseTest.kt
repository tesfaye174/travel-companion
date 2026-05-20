package com.travelcompanion.domain.usecase

import com.travelcompanion.domain.model.Trip
import com.travelcompanion.domain.model.TripType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.Date

class AnalyzePredictionUseCaseTest {

    private lateinit var useCase: AnalyzePredictionUseCase

    @Before
    fun setup() {
        useCase = AnalyzePredictionUseCase()
    }

    @Test
    fun `execute with empty trips returns zero prediction`() {
        val result = useCase.execute(emptyList(), emptyList())

        assertEquals(0.0, result.predictedKm, 0.001)
        assertEquals("Not enough data available.", result.message)
    }

    @Test
    fun `execute with single trip returns 1_2x average`() {
        val trips = listOf(
            createTrip(totalDistance = 100f)
        )

        val result = useCase.execute(trips, emptyList())

        // avg = 100, predicted = 100 * 1.2 = 120
        assertEquals(120.0, result.predictedKm, 0.001)
    }

    @Test
    fun `execute with multiple trips returns correct average times 1_2`() {
        val trips = listOf(
            createTrip(totalDistance = 50f),
            createTrip(totalDistance = 150f)
        )

        val result = useCase.execute(trips, emptyList())

        // avg = 100, predicted = 100 * 1.2 = 120
        assertEquals(120.0, result.predictedKm, 0.001)
    }

    @Test
    fun `execute with high prediction returns tireless traveler message`() {
        val trips = listOf(
            createTrip(totalDistance = 200f)
        )

        val result = useCase.execute(trips, emptyList())

        // predicted = 200 * 1.2 = 240 > 100
        assertTrue(result.message.contains("tireless traveler"))
    }

    @Test
    fun `execute with low prediction returns next month message`() {
        val trips = listOf(
            createTrip(totalDistance = 10f)
        )

        val result = useCase.execute(trips, emptyList())

        // predicted = 10 * 1.2 = 12 < 100
        assertTrue(result.message.contains("Next month"))
    }

    private fun createTrip(totalDistance: Float = 0f) = Trip(
        id = 1,
        title = "Test Trip",
        destination = "Test City",
        tripType = TripType.DAY_TRIP,
        startDate = Date(),
        endDate = Date(),
        totalDistance = totalDistance
    )
}
