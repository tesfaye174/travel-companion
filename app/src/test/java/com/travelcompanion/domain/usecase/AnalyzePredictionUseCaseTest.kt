package com.travelcompanion.domain.usecase

import com.travelcompanion.domain.model.Trip
import com.travelcompanion.domain.model.TripType
import org.junit.Assert.assertEquals
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
        val result = useCase.execute(emptyList())

        assertEquals(0.0, result.predictedKm, 0.001)
        assertEquals(PredictionMessage.NO_DATA, result.message)
    }

    @Test
    fun `execute with single trip returns 1_2x average`() {
        val trips = listOf(
            createTrip(totalDistance = 100f)
        )

        val result = useCase.execute(trips)

        // media = 100, previsione = 100 * 1.2 = 120
        assertEquals(120.0, result.predictedKm, 0.001)
    }

    @Test
    fun `execute with multiple trips returns correct average times 1_2`() {
        val trips = listOf(
            createTrip(totalDistance = 50f),
            createTrip(totalDistance = 150f)
        )

        val result = useCase.execute(trips)

        // media = 100, previsione = 100 * 1.2 = 120
        assertEquals(120.0, result.predictedKm, 0.001)
    }

    @Test
    fun `execute with high prediction returns tireless traveler message`() {
        val trips = listOf(
            createTrip(totalDistance = 200f)
        )

        val result = useCase.execute(trips)

        // previsione = 200 * 1.2 = 240 > 100
        assertEquals(PredictionMessage.TIRELESS_TRAVELER, result.message)
    }

    @Test
    fun `execute with low prediction returns next month message`() {
        val trips = listOf(
            createTrip(totalDistance = 10f)
        )

        val result = useCase.execute(trips)

        // previsione = 10 * 1.2 = 12 < 100
        assertEquals(PredictionMessage.NEXT_MONTH_ESTIMATE, result.message)
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
