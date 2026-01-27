package com.travelcompanion.utils

/**
 * App-wide constants to avoid magic numbers.
 */
object AppConstants {

    // GPS tracking config
    object Tracking {
        const val LOCATION_UPDATE_INTERVAL_MS = 5000L   // 5 sec
        const val LOCATION_FASTEST_INTERVAL_MS = 2000L  // 2 sec min
        const val LOCATION_MIN_DISTANCE_METERS = 10f
        const val TRACKING_NOTIFICATION_ID = 1
        const val TRACKING_CHANNEL_ID = "tracking_channel"
    }

    // Geofencing config
    object Geofencing {
        const val DEFAULT_RADIUS_METERS = 100f
        const val MAX_GEOFENCES = 100
        const val LOITERING_DELAY_MS = 300000  // 5 min
        const val GEOFENCE_CHANNEL_ID = "geofence_channel"
    }

    // Database
    object Database {
        const val DATABASE_NAME = "travel_companion_db"
        const val DATABASE_VERSION = 2
    }

    // UI stuff
    object UI {
        const val RECENT_TRIPS_COUNT = 5
        const val SHIMMER_DURATION_MS = 1000L
        const val SNACKBAR_UNDO_DURATION_MS = 5000
        const val MAP_DEFAULT_ZOOM = 12f
        const val MAP_POLYLINE_WIDTH = 8f
    }

    // Date formats
    object DateFormats {
        const val DATE_DISPLAY = "dd MMM yyyy"
        const val DATE_TIME_DISPLAY = "dd MMM yyyy, HH:mm"
        const val ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss"
        const val MONTH_YEAR = "yyyy-MM"
        const val TIME_ONLY = "HH:mm"
    }

    // Export config
    object Export {
        const val FILE_PREFIX = "travel_companion_export_"
        const val FILE_EXTENSION = ".json"
        const val JSON_MIME_TYPE = "application/json"
    }

    // Intent extras
    object IntentExtras {
        const val EXTRA_TRIP_ID = "extra_trip_id"
        const val EXTRA_DESTINATION = "destination"
        const val EXTRA_JOURNEY_ID = "extra_journey_id"
    }

    // DataStore keys
    object PreferenceKeys {
        const val NOTIFY_POI = "notify_poi"
        const val NOTIFY_REMINDERS = "notify_reminders"
        const val AUTO_TRACKING = "auto_tracking"
        const val DISTANCE_UNIT = "distance_unit"
        const val THEME_MODE = "theme_mode"
    }

    // Platform geofence action for non-Play-Services provider
    object PlatformIntents {
        const val ACTION_PLATFORM_GEOFENCE = "com.travelcompanion.PLATFORM_GEOFENCE_EVENT"
        const val EXTRA_GEOFENCE_ID = "extra_geofence_id"
        const val EXTRA_TRANSITION = "extra_geofence_transition"
    }

    // WorkManager
    object WorkManager {
        const val REMINDER_WORK_NAME = "trip_reminder_work"
        const val REMINDER_INTERVAL_HOURS = 24L
    }

    // Validation limits
    object Validation {
        const val MAX_TITLE_LENGTH = 100
        const val MAX_DESTINATION_LENGTH = 200
        const val MAX_NOTE_LENGTH = 5000
        const val MIN_TITLE_LENGTH = 1
    }
}
