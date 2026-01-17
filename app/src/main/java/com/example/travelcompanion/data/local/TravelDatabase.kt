package com.example.travelcompanion.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.travelcompanion.domain.model.*

@Database(
    entities = [Trip::class, Journey::class, LocationPoint::class, Photo::class, Note::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TravelDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
    abstract fun journeyDao(): JourneyDao
    abstract fun locationPointDao(): LocationPointDao
    abstract fun photoDao(): PhotoDao
    abstract fun noteDao(): NoteDao

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