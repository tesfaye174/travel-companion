package com.travelcompanion.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.travelcompanion.data.db.converters.Converters
import com.travelcompanion.data.db.entities.JourneyEntity
import com.travelcompanion.data.db.entities.PhotoNoteEntity
import com.travelcompanion.data.db.entities.TripEntity

@Database(
    entities = [TripEntity::class, JourneyEntity::class, PhotoNoteEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TravelDatabase : RoomDatabase() {
    abstract fun tripDao(): com.travelcompanion.data.db.dao.TripDao
    abstract fun journeyDao(): com.travelcompanion.data.db.dao.JourneyDao
    abstract fun photoDao(): com.travelcompanion.data.db.dao.PhotoNoteDao

    companion object {
        @Volatile
        private var INSTANCE: TravelDatabase? = null

        fun getDatabase(context: Context): TravelDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TravelDatabase::class.java,
                    "travel_companion_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

