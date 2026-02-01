package com.travelcompanion.utils

/**
 * Costanti globali dell'applicazione per evitare "magic numbers" nel codice.
 *
 * Raggruppare le costanti in un unico file ha diversi vantaggi:
 * - Facile trovare e modificare valori di configurazione
 * - Evita duplicazione di stringhe/numeri nel codice
 * - Migliora la leggibilità del codice
 *
 * Ho organizzato le costanti in object annidati per categoria logica.
 */
object AppConstants {

    /**
     * Configurazione per il tracking GPS.
     * Questi valori influenzano precisione e consumo batteria.
     */
    object Tracking {
        const val LOCATION_UPDATE_INTERVAL_MS = 5000L   // Ogni 5 secondi chiedo un aggiornamento
        const val LOCATION_FASTEST_INTERVAL_MS = 2000L  // Ma non più frequente di 2 sec
        const val LOCATION_MIN_DISTANCE_METERS = 10f    // Ignoro movimenti < 10m (rumore GPS)
        const val TRACKING_NOTIFICATION_ID = 1          // ID per la notifica del foreground service
        const val TRACKING_CHANNEL_ID = "tracking_channel"
    }

    /**
     * Configurazione del geofencing (notifiche basate sulla posizione).
     * Il geofencing consuma poca batteria perché usa eventi del sistema.
     */
    object Geofencing {
        const val DEFAULT_RADIUS_METERS = 100f  // Raggio predefinito delle aree monitorate
        const val MAX_GEOFENCES = 100           // Limite imposto da Android
        const val LOITERING_DELAY_MS = 300000   // 5 min di permanenza prima della notifica
        const val GEOFENCE_CHANNEL_ID = "geofence_channel"
    }

    /**
     * Configurazione del database Room.
     */
    object Database {
        const val DATABASE_NAME = "travel_companion_db"
        const val DATABASE_VERSION = 2  // Incrementato quando aggiungo colonne/tabelle
    }

    /**
     * Costanti per la UI - valori usati nelle varie schermate.
     */
    object UI {
        const val RECENT_TRIPS_COUNT = 5        // Viaggi mostrati nella home
        const val SHIMMER_DURATION_MS = 1000L   // Durata effetto loading
        const val SNACKBAR_UNDO_DURATION_MS = 5000  // Tempo per annullare cancellazione
        const val MAP_DEFAULT_ZOOM = 12f        // Livello zoom iniziale mappa
        const val MAP_POLYLINE_WIDTH = 8f       // Spessore linea percorso
    }

    /**
     * Pattern per la formattazione delle date.
     * Uso il pattern italiano/europeo (giorno/mese/anno).
     */
    object DateFormats {
        const val DATE_DISPLAY = "dd MMM yyyy"           // Es: "15 Gen 2024"
        const val DATE_TIME_DISPLAY = "dd MMM yyyy, HH:mm"
        const val ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss"     // Per export/import dati
        const val MONTH_YEAR = "yyyy-MM"
        const val TIME_ONLY = "HH:mm"
    }

    /**
     * Configurazione per l'export dei dati.
     */
    object Export {
        const val FILE_PREFIX = "travel_companion_export_"
        const val FILE_EXTENSION = ".json"
        const val JSON_MIME_TYPE = "application/json"
    }

    /**
     * Chiavi per gli Intent extras usati nella navigazione.
     */
    object IntentExtras {
        const val EXTRA_TRIP_ID = "extra_trip_id"
        const val EXTRA_DESTINATION = "destination"
        const val EXTRA_JOURNEY_ID = "extra_journey_id"
    }

    /**
     * Chiavi per le preferenze salvate in DataStore.
     */
    object PreferenceKeys {
        const val NOTIFY_POI = "notify_poi"
        const val NOTIFY_REMINDERS = "notify_reminders"
        const val AUTO_TRACKING = "auto_tracking"
        const val DISTANCE_UNIT = "distance_unit"
        const val THEME_MODE = "theme_mode"
    }

    /**
     * Action e extras per il geofencing senza Play Services.
     * Usati dal PlatformGeofenceProvider come alternativa.
     */
    object PlatformIntents {
        const val ACTION_PLATFORM_GEOFENCE = "com.travelcompanion.PLATFORM_GEOFENCE_EVENT"
        const val EXTRA_GEOFENCE_ID = "extra_geofence_id"
        const val EXTRA_TRANSITION = "extra_geofence_transition"
    }

    /**
     * Configurazione per WorkManager (task in background).
     */
    object WorkManager {
        const val REMINDER_WORK_NAME = "trip_reminder_work"
        const val REMINDER_INTERVAL_HOURS = 24L
    }

    /**
     * Limiti di validazione per l'input utente.
     * Prevengono input eccessivamente lunghi o vuoti.
     */
    object Validation {
        const val MAX_TITLE_LENGTH = 100
        const val MAX_DESTINATION_LENGTH = 200
        const val MAX_NOTE_LENGTH = 5000
        const val MIN_TITLE_LENGTH = 1
    }
}
