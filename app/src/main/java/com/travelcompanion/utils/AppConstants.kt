package com.travelcompanion.utils

/**
 * Costanti a livello applicazione per evitare numeri e stringhe magiche.
 * Configurazione centralizzata per facile manutenzione e consistenza.
 */
object AppConstants {

    /**
     * Costanti di configurazione per il tracciamento GPS.
     */
    object Tracking {
        /** Intervallo per gli aggiornamenti posizione in millisecondi (5 secondi) */
        const val LOCATION_UPDATE_INTERVAL_MS = 5000L

        /** Intervallo minimo tra aggiornamenti posizione in millisecondi (2 secondi) */
        const val LOCATION_FASTEST_INTERVAL_MS = 2000L

        /** Distanza minima di cambio per aggiornamenti posizione in metri */
        const val LOCATION_MIN_DISTANCE_METERS = 10f

        /** ID notifica per il servizio in foreground */
        const val TRACKING_NOTIFICATION_ID = 1

        /** ID canale notifica per il tracciamento */
        const val TRACKING_CHANNEL_ID = "tracking_channel"
    }

    /**
     * Costanti di configurazione per il geofencing.
     */
    object Geofencing {
        /** Raggio geofence predefinito in metri */
        const val DEFAULT_RADIUS_METERS = 100f

        /** Numero massimo di geofence attivi */
        const val MAX_GEOFENCES = 100

        /** Ritardo di loitering del geofence in millisecondi (5 minuti) */
        const val LOITERING_DELAY_MS = 300000

        /** ID canale notifica per avvisi geofence */
        const val GEOFENCE_CHANNEL_ID = "geofence_channel"
    }

    /**
     * Costanti di configurazione del database.
     */
    object Database {
        /** Nome del database */
        const val DATABASE_NAME = "travel_companion_db"

        /** Versione attuale del database */
        const val DATABASE_VERSION = 2
    }

    /**
     * Costanti di configurazione dell'interfaccia utente.
     */
    object UI {
        /** Numero di viaggi recenti da mostrare nella home */
        const val RECENT_TRIPS_COUNT = 5

        /** Durata animazione shimmer in millisecondi */
        const val SHIMMER_DURATION_MS = 1000L

        /** Durata snackbar per azioni annulla */
        const val SNACKBAR_UNDO_DURATION_MS = 5000

        /** Livello di zoom predefinito della mappa */
        const val MAP_DEFAULT_ZOOM = 12f

        /** Larghezza polyline del percorso sulla mappa */
        const val MAP_POLYLINE_WIDTH = 8f
    }

    /**
     * Pattern di formato data e ora.
     */
    object DateFormats {
        /** Formato data completo (es. "15 Gen 2026") */
        const val DATE_DISPLAY = "dd MMM yyyy"

        /** Formato data e ora (es. "15 Gen 2026, 14:30") */
        const val DATE_TIME_DISPLAY = "dd MMM yyyy, HH:mm"

        /** Formato ISO 8601 per esportazione/importazione */
        const val ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss"

        /** Formato mese-anno per statistiche (es. "2026-01") */
        const val MONTH_YEAR = "yyyy-MM"

        /** Formato solo ora (es. "14:30") */
        const val TIME_ONLY = "HH:mm"
    }

    /**
     * Configurazione file ed esportazione.
     */
    object Export {
        /** Prefisso nome file di esportazione */
        const val FILE_PREFIX = "travel_companion_export_"

        /** Estensione file di esportazione */
        const val FILE_EXTENSION = ".json"

        /** MIME type per file JSON */
        const val JSON_MIME_TYPE = "application/json"
    }

    /**
     * Chiavi extras degli Intent.
     */
    object IntentExtras {
        /** Chiave per ID viaggio negli intent */
        const val EXTRA_TRIP_ID = "extra_trip_id"

        /** Chiave per destinazione negli intent */
        const val EXTRA_DESTINATION = "destination"

        /** Chiave per ID percorso negli intent */
        const val EXTRA_JOURNEY_ID = "extra_journey_id"
    }

    /**
     * Chiavi SharedPreferences/DataStore.
     */
    object PreferenceKeys {
        /** Chiave per preferenza notifiche POI */
        const val NOTIFY_POI = "notify_poi"

        /** Chiave per preferenza promemoria viaggi */
        const val NOTIFY_REMINDERS = "notify_reminders"

        /** Chiave per preferenza auto-tracciamento */
        const val AUTO_TRACKING = "auto_tracking"

        /** Chiave per preferenza unità di distanza */
        const val DISTANCE_UNIT = "distance_unit"

        /** Chiave per preferenza modalità tema */
        const val THEME_MODE = "theme_mode"
    }

    /**
     * Configurazione WorkManager.
     */
    object WorkManager {
        /** Nome univoco del lavoro per promemoria viaggi */
        const val REMINDER_WORK_NAME = "trip_reminder_work"

        /** Intervallo promemoria in ore */
        const val REMINDER_INTERVAL_HOURS = 24L
    }

    /**
     * Vincoli di validazione.
     */
    object Validation {
        /** Lunghezza massima titolo viaggio */
        const val MAX_TITLE_LENGTH = 100

        /** Lunghezza massima destinazione */
        const val MAX_DESTINATION_LENGTH = 200

        /** Lunghezza massima contenuto nota */
        const val MAX_NOTE_LENGTH = 5000

        /** Lunghezza minima titolo viaggio */
        const val MIN_TITLE_LENGTH = 1
    }
}
