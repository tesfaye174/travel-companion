package com.travelcompanion.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

object PermissionUtils {

    // Solo i permessi foreground: ACCESS_BACKGROUND_LOCATION NON va messo qui.
    // Da Android 11 il sistema ignora le richieste che mischiano background e foreground,
    // e tenerlo nel controllo faceva fallire hasLocationPermissions() anche con il
    // permesso "Mentre usi l'app" concesso (la mappa non si centrava mai).
    val LOCATION_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    fun hasLocationPermissions(context: Context): Boolean {
        return LOCATION_PERMISSIONS.all { permission ->
            ContextCompat.checkSelfPermission(context, permission) ==
                    PackageManager.PERMISSION_GRANTED
        }
    }
}
