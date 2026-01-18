package com.example.travelcompanion.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.travelcompanion.data.db.converters.Converters
import com.example.travelcompanion.data.db.dao.JourneyDao
import com.example.travelcompanion.data.db.dao.PhotoNoteDao
import com.example.travelcompanion.data.db.dao.TripDao
import com.example.travelcompanion.data.db.entities.JourneyEntity
import com.example.travelcompanion.data.db.entities.PhotoNoteEntity
import com.example.travelcompanion.data.db.entities.TripEntity

@Database(
    entities = [TripEntity::class, JourneyEntity::class, PhotoNoteEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
    abstract fun journeyDao(): JourneyDao
    abstract fun photoNoteDao(): PhotoNoteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "travel_companion_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}