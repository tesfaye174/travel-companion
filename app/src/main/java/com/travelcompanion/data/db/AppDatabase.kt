package com.travelcompanion.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
 * Using Hilt for injection - see DatabaseModule.
 *
 * Schema export enabled for migration tracking.
 * Version 2: Current schema
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
    version = 4,
    exportSchema = true
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
        const val DATABASE_NAME = "travel_companion_db"

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Schema was already at version 2 in earlier releases; no DDL needed.
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE INDEX IF NOT EXISTS `idx_trip_start_date` ON `trips` (`start_date`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `idx_trip_type` ON `trips` (`trip_type`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `idx_trip_is_tracking` ON `trips` (`is_tracking`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `idx_trip_destination` ON `trips` (`destination`)")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `notes` ADD COLUMN `title` TEXT NOT NULL DEFAULT ''")
            }
        }
    }
}

