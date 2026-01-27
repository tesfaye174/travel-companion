package com.travelcompanion.data.db.dao

import androidx.room.*
import com.travelcompanion.data.db.entities.TripEntity
import com.travelcompanion.domain.model.TripType
import com.travelcompanion.domain.model.MonthlyStat
import com.travelcompanion.domain.model.TripTypeStat
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {

    // CRUD Operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: TripEntity): Long

    @Update
    suspend fun updateTrip(trip: TripEntity)

    @Delete
    suspend fun deleteTrip(trip: TripEntity)

    @Query("DELETE FROM trips WHERE id = :tripId")
    suspend fun deleteTripById(tripId: Long): Int

    // Queries with Flow
    @Query("SELECT * FROM trips WHERE id = :id")
    fun getTripByIdFlow(id: Long): Flow<TripEntity?>

    @Query("SELECT * FROM trips ORDER BY start_date DESC")
    fun getAllTripsFlow(): Flow<List<TripEntity>>

    @Query("SELECT * FROM trips WHERE trip_type = :type ORDER BY start_date DESC")
    fun getTripsByTypeFlow(type: TripType): Flow<List<TripEntity>>

    // Paged queries
    @Query("SELECT * FROM trips ORDER BY start_date DESC LIMIT :limit OFFSET :offset")
    suspend fun getAllTripsPaged(limit: Int, offset: Int): List<TripEntity>

    @Query("SELECT * FROM trips WHERE trip_type = :type ORDER BY start_date DESC LIMIT :limit OFFSET :offset")
    suspend fun getTripsByTypePaged(type: String, limit: Int, offset: Int): List<TripEntity>

    // Cached queries with refresh
    @Transaction
    @Query("SELECT * FROM trips WHERE id = :id")
    fun getTripWithDetails(id: Long): Flow<TripWithDetails>

    @Transaction
    @Query("SELECT * FROM trips WHERE start_date BETWEEN :startDate AND :endDate")
    fun getTripsBetweenDatesFlow(startDate: Long, endDate: Long): Flow<List<TripEntity>>

    @Query("SELECT * FROM trips WHERE start_date BETWEEN :startDate AND :endDate")
    suspend fun getTripsBetweenDates(startDate: Long, endDate: Long): List<TripEntity>

    // Statistics with caching
    @Query("""
        SELECT
            COUNT(*) as total,
            SUM(total_distance) as totalDistance,
            AVG(total_duration) as avgDuration
        FROM trips
        WHERE strftime('%Y', datetime(start_date / 1000, 'unixepoch')) = :year
    """)
    fun getYearlyStats(year: String): Flow<YearlyStats>

    // Search with FTS (Full Text Search)
    @Query("""
        SELECT * FROM trips
        WHERE title LIKE '%' || :query || '%'
           OR destination LIKE '%' || :query || '%'
           OR notes LIKE '%' || :query || '%'
        ORDER BY
            CASE
                WHEN title LIKE :query || '%' THEN 1
                WHEN destination LIKE :query || '%' THEN 2
                ELSE 3
            END,
            start_date DESC
    """)
    fun searchTrips(query: String): Flow<List<TripEntity>>

    // Batch operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrips(trips: List<TripEntity>): List<Long>

    @Update
    suspend fun updateTrips(trips: List<TripEntity>)

    @Delete
    suspend fun deleteTrips(trips: List<TripEntity>)

    // Data classes for complex queries
    data class TripWithDetails(
        @Embedded val trip: TripEntity,
        @Relation(
            parentColumn = "id",
            entityColumn = "trip_id"
        )
        val journeys: List<com.travelcompanion.data.db.entities.JourneyEntity>,
        @Relation(
            parentColumn = "id",
            entityColumn = "trip_id"
        )
        val photoNotes: List<com.travelcompanion.data.db.entities.PhotoNoteEntity>
    )

    data class YearlyStats(
        val total: Int,
        val totalDistance: Float?,
        val avgDuration: Long?
    )

    // Aggregate / statistics helpers
    @Query("SELECT COALESCE(SUM(total_distance), 0) FROM trips")
    fun getTotalDistance(): Float

    @Query("SELECT COALESCE(SUM(total_duration), 0) FROM trips")
    fun getTotalDuration(): Long

    @Query("SELECT COUNT(*) FROM trips")
    fun getTripCount(): Int

    @Query("""
         SELECT strftime('%m', datetime(start_date / 1000, 'unixepoch')) as month,
             COUNT(*) as tripCount,
             COALESCE(SUM(total_distance), 0) as totalDistance,
             COALESCE(SUM(total_duration), 0) as totalDuration
        FROM trips
        GROUP BY month
        ORDER BY month
    """)
    fun getMonthlyStats(): List<MonthlyStat>

    @Query("""
        SELECT trip_type as type,
               COUNT(*) as count,
               (COUNT(*) * 100.0 / (SELECT COUNT(*) FROM trips)) as percentage
        FROM trips
        GROUP BY trip_type
    """)
    fun getTripTypeStats(): List<TripTypeStat>
}
