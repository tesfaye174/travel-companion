package com.travelcompanion.data.repository

import com.travelcompanion.data.db.AppDatabase
import com.travelcompanion.data.db.dao.TripDao
import com.travelcompanion.domain.model.Trip
import com.travelcompanion.domain.model.TripType
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.Date

/**
 * Verifica che il repository rifiuti i dati non validi PRIMA di toccare il DB.
 * I fragment validano già l'input dell'utente, ma il repository è l'unico punto
 * da cui passano tutte le scritture (anche future), quindi la validazione va qui.
 */
class TripRepositoryTest {

    private lateinit var tripDao: TripDao
    private lateinit var database: AppDatabase
    private lateinit var repository: TripRepository

    @Before
    fun setup() {
        // relaxed: dei DAO ci interessa solo verificare se vengono chiamati o no
        tripDao = mockk(relaxed = true)
        database = mockk {
            every { tripDao() } returns this@TripRepositoryTest.tripDao
            every { journeyDao() } returns mockk(relaxed = true)
            every { photoNoteDao() } returns mockk(relaxed = true)
            every { noteDao() } returns mockk(relaxed = true)
            every { geofenceAreaDao() } returns mockk(relaxed = true)
            every { geofenceEventDao() } returns mockk(relaxed = true)
        }
        repository = TripRepository(database)
    }

    private fun validTrip(id: Long = 0) = Trip(
        id = id,
        title = "Gita al lago",
        destination = "Lago di Como",
        tripType = TripType.DAY_TRIP,
        startDate = Date(1000L),
        endDate = Date(2000L)
    )

    @Test
    fun `insertTrip rifiuta titolo vuoto senza toccare il DB`() = runTest {
        val esito = runCatching { repository.insertTrip(validTrip().copy(title = "   ")) }
        assertTrue(esito.exceptionOrNull() is IllegalArgumentException)
        coVerify(exactly = 0) { tripDao.insertTrip(any()) }
    }

    @Test
    fun `insertTrip rifiuta endDate precedente a startDate`() = runTest {
        val esito = runCatching {
            repository.insertTrip(validTrip().copy(startDate = Date(2000L), endDate = Date(1000L)))
        }
        assertTrue(esito.exceptionOrNull() is IllegalArgumentException)
        coVerify(exactly = 0) { tripDao.insertTrip(any()) }
    }

    @Test
    fun `insertTrip valido arriva al DAO`() = runTest {
        repository.insertTrip(validTrip())
        coVerify(exactly = 1) { tripDao.insertTrip(any()) }
    }

    @Test
    fun `updateTrip rifiuta un trip senza id`() = runTest {
        // id = 0 vuol dire che il trip non è mai stato salvato: aggiornarlo è un bug del chiamante
        val esito = runCatching { repository.updateTrip(validTrip(id = 0)) }
        assertTrue(esito.exceptionOrNull() is IllegalArgumentException)
        coVerify(exactly = 0) { tripDao.updateTrip(any()) }
    }

    @Test
    fun `updateTrip valido arriva al DAO`() = runTest {
        repository.updateTrip(validTrip(id = 7))
        coVerify(exactly = 1) { tripDao.updateTrip(any()) }
    }

    @Test
    fun `deleteTrip rifiuta un trip senza id`() = runTest {
        val esito = runCatching { repository.deleteTrip(validTrip(id = 0)) }
        assertTrue(esito.exceptionOrNull() is IllegalArgumentException)
        coVerify(exactly = 0) { tripDao.deleteTrip(any()) }
    }
}
