package com.travelcompanion.domain.repository

import com.travelcompanion.domain.model.Trip
import com.travelcompanion.domain.model.TripType
import com.travelcompanion.domain.model.Journey
import com.travelcompanion.domain.model.PhotoNote
import com.travelcompanion.domain.model.Note
import com.travelcompanion.domain.model.GeofenceArea
import com.travelcompanion.domain.model.GeofenceEvent
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * Interfaccia Repository che definisce tutte le operazioni sui dati per l'app Travel Companion.
 * 
 * Questa interfaccia segue il pattern Repository della Clean Architecture,
 * astraendo il layer dati dal layer dominio. Fornisce metodi per gestire
 * viaggi, percorsi, foto, note e dati di geofencing.
 * 
 * Tutti i metodi che restituiscono Flow sono progettati per aggiornamenti reattivi della UI,
 * mentre le funzioni suspend sono usate per operazioni singole.
 * 
 * @see TripRepository per l'implementazione concreta
 */
interface ITripRepository {
    // Operazioni sui viaggi
    /**
     * Inserisce un nuovo viaggio nel database.
     * @param trip Il viaggio da inserire
     * @return L'ID del viaggio appena inserito
     */
    suspend fun insertTrip(trip: Trip): Long
    
    /**
     * Aggiorna un viaggio esistente nel database.
     * @param trip Il viaggio con i dati aggiornati
     */
    suspend fun updateTrip(trip: Trip)
    
    /**
     * Elimina un viaggio dal database.
     * @param trip Il viaggio da eliminare
     */
    suspend fun deleteTrip(trip: Trip)
    
    /**
     * Recupera un viaggio tramite il suo identificatore univoco.
     * @param id L'ID del viaggio
     * @return Il viaggio se trovato, null altrimenti
     */
    suspend fun getTripById(id: Long): Trip?
    
    /**
     * Restituisce un Flow di tutti i viaggi, ordinati per data di inizio decrescente.
     * @return Flow che emette la lista di tutti i viaggi
     */
    fun getAllTrips(): Flow<List<Trip>>
    
    /**
     * Restituisce un Flow di viaggi filtrati per tipo.
     * @param type Il tipo di viaggio per cui filtrare
     * @return Flow che emette i viaggi del tipo specificato
     */
    fun getTripsByType(type: TripType): Flow<List<Trip>>
    
    /**
     * Restituisce un Flow di viaggi in un intervallo di date.
     * @param start La data di inizio (inclusa)
     * @param end La data di fine (inclusa)
     * @return Flow che emette i viaggi nell'intervallo di date
     */
    fun getTripsBetweenDates(start: Date, end: Date): Flow<List<Trip>>

    // Operazioni sui percorsi
    suspend fun insertJourney(journey: Journey): Long
    suspend fun updateJourney(journey: Journey)
    suspend fun deleteJourney(journey: Journey)
    fun getJourneysByTripId(tripId: Long): Flow<List<Journey>>
    fun getAllJourneys(): Flow<List<Journey>>

    // Operazioni sulle foto con note
    suspend fun insertPhotoNote(photoNote: PhotoNote): Long
    suspend fun updatePhotoNote(photoNote: PhotoNote)
    suspend fun deletePhotoNote(photoNote: PhotoNote)
    fun getPhotoNotesByTripId(tripId: Long): Flow<List<PhotoNote>>

    // Operazioni sulle note (note testuali allegate a momenti/luoghi)
    suspend fun insertNote(note: Note): Long
    suspend fun updateNote(note: Note)
    suspend fun deleteNote(note: Note)
    fun getNotesByTripId(tripId: Long): Flow<List<Note>>

    // Geofencing (aree + eventi)
    suspend fun upsertGeofenceArea(id: String, name: String, lat: Double, lon: Double, radiusMeters: Float)
    fun getGeofenceAreas(): Flow<List<GeofenceArea>>
    fun getGeofenceEvents(): Flow<List<GeofenceEvent>>

    // Statistiche
    suspend fun getTotalDistance(): Float
    suspend fun getTotalDuration(): Long
    suspend fun getTripCount(): Int
    suspend fun getMonthlyStats(): List<MonthlyStat>
    suspend fun getTripTypeStats(): List<TripTypeStat>

    data class MonthlyStat(
        val month: String,
        val tripCount: Int,
        val totalDistance: Float,
        val totalDuration: Long
    )

    data class TripTypeStat(
        val type: TripType,
        val count: Int,
        val percentage: Float
    )
}

