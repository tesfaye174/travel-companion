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
 * Database principale dell'applicazione basato su Room.
 *
 * Room è un layer di astrazione sopra SQLite che fornisce:
 * - Verifica delle query SQL a compile-time
 * - Conversione automatica tra oggetti Kotlin e tabelle SQL
 * - Supporto nativo per Flow e LiveData
 *
 * Questo database contiene 6 tabelle per gestire:
 * - Viaggi (trips) - i viaggi creati dall'utente
 * - Percorsi (journeys) - i tracciati GPS registrati durante il tracking
 * - Foto con note (photo_notes) - foto scattate durante i viaggi
 * - Note testuali (notes) - appunti dell'utente
 * - Aree geofence (geofence_areas) - zone di interesse per le notifiche
 * - Eventi geofence (geofence_events) - log degli ingressi/uscite dalle zone
 *
 * La versione del database è 2 (incrementata dopo aggiunte di colonne).
 * exportSchema = false perché non sto usando le migrazioni automatiche di Room.
 *
 * @see <a href="https://developer.android.com/training/data-storage/room">Room Database</a>
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
        // Variabile volatile per il pattern Singleton thread-safe.
        // @Volatile garantisce che le modifiche siano visibili a tutti i thread.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Restituisce l'istanza singleton del database.
         *
         * Questo metodo è necessario per i componenti che non supportano
         * l'iniezione di dipendenze con Hilt, come:
         * - BroadcastReceiver (GeofenceBroadcastReceiver)
         * - Worker di WorkManager (GeofenceRegistrationWorker)
         *
         * Uso il pattern double-checked locking con synchronized per
         * garantire che venga creata una sola istanza anche in caso
         * di accessi concorrenti da thread diversi.
         *
         * fallbackToDestructiveMigration() ricrea il DB se la versione cambia.
         * È accettabile per questa app dato che i dati possono essere ricreati.
         * In un'app di produzione userei migrazioni vere.
         */
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

