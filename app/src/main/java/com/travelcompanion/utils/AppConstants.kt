package com.travelcompanion.utils

/**
 * Costanti condivise tra più componenti, per evitare numeri magici sparsi.
 * Le costanti usate da una classe sola stanno nella classe che le usa.
 */
object AppConstants {

    // ID delle notifiche, tenuti insieme per non rischiare collisioni
    object NotificationIds {
        const val TRACKING = 1001
        const val GEOFENCE = 1002
        const val REMINDER = 1003
    }

    // ID dei canali di notifica (Android 8+)
    object NotificationChannels {
        const val TRACKING = "tracking_channel"
        const val GEOFENCE = "geofence_channel"
        const val REMINDERS = "travel_reminders"
    }

    // Parametri delle richieste di posizione dei LocationProvider
    object Tracking {
        const val LOCATION_UPDATE_INTERVAL_MS = 5000L   // 5 sec
        const val LOCATION_FASTEST_INTERVAL_MS = 2000L  // 2 sec min
        const val LOCATION_MIN_DISTANCE_METERS = 10f
    }

    // Action e extra usati dal provider geofence senza Play Services,
    // condivisi con il GeofenceBroadcastReceiver che li riceve
    object PlatformIntents {
        const val ACTION_PLATFORM_GEOFENCE = "com.travelcompanion.PLATFORM_GEOFENCE_EVENT"
        const val EXTRA_GEOFENCE_ID = "extra_geofence_id"
        const val EXTRA_TRANSITION = "extra_geofence_transition"
    }
}
