package com.example.travelcompanion.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.travelcompanion.data.db.dao.*
import com.example.travelcompanion.data.db.entities.*

@Database(
    entities = [
        TripEntity::class,
        JourneyEntity::class,
        PointEntity::class,
        NoteEntity::class,
        PhotoEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
    abstract fun journeyDao(): JourneyDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "travel_companion_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
