package com.travelcompanion.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.travelcompanion.data.db.converters.Converters
import com.travelcompanion.data.db.dao.JourneyDao
import com.travelcompanion.data.db.dao.GeofenceAreaDao
import com.travelcompanion.data.db.dao.GeofenceEventDao
import com.travelcompanion.data.db.dao.NoteDao
import com.travelcompanion.data.db.dao.PhotoNoteDao
import com.travelcompanion.data.db.dao.TripDao
import com.travelcompanion.data.db.entities.GeofenceAreaEntity
import com.travelcompanion.data.db.entities.GeofenceEventEntity
import com.travelcompanion.data.db.entities.JourneyEntity
import com.travelcompanion.data.db.entities.NoteEntity
import com.travelcompanion.data.db.entities.PhotoNoteEntity
import com.travelcompanion.data.db.entities.TripEntity

/**
 * Room database with all DAOs.
 * Using Hilt for injection - see AppModule.
 */
@Database(
    entities = [
        TripEntity::class,
        JourneyEntity::class,
        PhotoNoteEntity::class,
        NoteEntity::class,
        GeofenceAreaEntity::class,
        GeofenceEventEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
    abstract fun journeyDao(): JourneyDao
    abstract fun photoNoteDao(): PhotoNoteDao
    abstract fun noteDao(): NoteDao
    abstract fun geofenceAreaDao(): GeofenceAreaDao
    abstract fun geofenceEventDao(): GeofenceEventDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // For non-Hilt components like Workers and BroadcastReceivers
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "travel_companion_db"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

