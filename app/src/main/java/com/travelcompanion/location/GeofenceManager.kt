package com.travelcompanion.location

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

@Singleton
@Suppress("unused")
class GeofenceManager @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val geofencingClient: GeofencingClient = LocationServices.getGeofencingClient(context)

    // Manteniamo un PendingIntent singleton per evitare molteplici creazioni
    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(context, com.travelcompanion.utils.GeofenceBroadcastReceiver::class.java)

        // Calcoliamo i flag in modo compatibile con la API level
        val baseFlag = PendingIntent.FLAG_UPDATE_CURRENT
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            baseFlag or PendingIntent.FLAG_IMMUTABLE
        } else {
            baseFlag
        }

        PendingIntent.getBroadcast(
            context,
            0,
            intent,
            flags
        )
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    @SuppressLint("MissingPermission")
    fun addGeofence(
        geofenceId: String,
        lat: Double,
        lng: Double,
        radiusMeters: Float,
        transitionType: Int = (Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
    ) {
        val geofence = Geofence.Builder()
            .setRequestId(geofenceId)
            .setCircularRegion(lat, lng, radiusMeters)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(transitionType)
            .build()

        val request = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        // Verifico i permessi prima di procedere: per ricevere eventi in background
        // su Android 10+ serve ACCESS_BACKGROUND_LOCATION: qui loggo se manca.
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Timber.w("Geofence: ACCESS_BACKGROUND_LOCATION non concesso; eventi in background potrebbero non arrivare")
            }

            geofencingClient.addGeofences(request, geofencePendingIntent)
                .addOnSuccessListener { Timber.d("Geofence: Aggiunto con successo") }
                .addOnFailureListener { Timber.e(it, "Geofence: Errore aggiunta") }
        } else {
            Timber.w("Geofence: Permesso ACCESS_FINE_LOCATION non concesso: impossibile aggiungere geofence")
        }
    }

    // Nota: ora usiamo il PendingIntent singleton `geofencePendingIntent` definito sopra.
    // Questa funzione veniva usata in passato e rimane per compatibilità, ma non è più necessaria.
    // private fun getGeofencePendingIntent(): PendingIntent = geofencePendingIntent
}
