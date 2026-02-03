package com.travelcompanion.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
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
 * Database Room dell'app.
 * Contiene le tabelle per viaggi, percorsi, foto, note e geofence.
 * Versione 2 perche ho aggiunto delle colonne dopo la prima release.
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

        // Migration example from version 1 to 2: add columns introduced in v2
        // Adjust SQL if your original v1 schema differs. These ALTER TABLE statements
        // add nullable columns or columns with default values so existing rows are preserved.
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Helper per controllare se una colonna esiste
                fun columnExists(table: String, column: String): Boolean {
                    val cursor = database.query("PRAGMA table_info($table)")
                    cursor.use {
                        val idxName = it.getColumnIndex("name")
                        while (it.moveToNext()) {
                            if (idxName >= 0) {
                                val name = it.getString(idxName)
                                if (name == column) return true
                            }
                        }
                    }
                    return false
                }

                if (!columnExists("trips", "photo_count")) {
                    database.execSQL("ALTER TABLE trips ADD COLUMN photo_count INTEGER NOT NULL DEFAULT 0")
                }
                if (!columnExists("trips", "notes")) {
                    database.execSQL("ALTER TABLE trips ADD COLUMN notes TEXT NOT NULL DEFAULT ''")
                }
                if (!columnExists("trips", "is_tracking")) {
                    database.execSQL("ALTER TABLE trips ADD COLUMN is_tracking INTEGER NOT NULL DEFAULT 0")
                }
                if (!columnExists("trips", "destination_latitude")) {
                    database.execSQL("ALTER TABLE trips ADD COLUMN destination_latitude REAL")
                }
                if (!columnExists("trips", "destination_longitude")) {
                    database.execSQL("ALTER TABLE trips ADD COLUMN destination_longitude REAL")
                }
            }
        }

        // singleton per i componenti che non supportano hilt
        // tipo broadcast receiver e worker
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "travel_companion_db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
