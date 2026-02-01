package com.travelcompanion.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.tasks.CancellationTokenSource

/**
 * Utility object per le operazioni relative alla localizzazione GPS.
 *
 * Uso il FusedLocationProvider di Google Play Services perché:
 * - È più preciso del LocationManager standard di Android
 * - Gestisce automaticamente la selezione della fonte migliore (GPS, WiFi, celle)
 * - È più efficiente in termini di batteria
 *
 * Per dispositivi senza Play Services (es. Huawei), l'app usa un provider
 * alternativo (PlatformLocationProvider) configurato nel modulo Hilt.
 */
object LocationUtils {

    /**
     * Verifica se l'app ha i permessi di localizzazione necessari.
     *
     * Servono entrambi i permessi (FINE e COARSE) per garantire
     * il funzionamento sia del tracking preciso che delle funzioni base.
     *
     * @return true se entrambi i permessi sono stati concessi
     */
    fun hasLocationPermission(context: Context): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Ottiene la posizione corrente dell'utente in modo asincrono.
     *
     * Usa PRIORITY_HIGH_ACCURACY per ottenere la posizione GPS più precisa.
     * Il CancellationTokenSource permette di cancellare la richiesta se necessario.
     *
     * @param context Context per verificare i permessi
     * @param fusedLocationClient Client di Play Services per la localizzazione
     * @param onSuccess Callback chiamato con la Location ottenuta
     * @param onFailure Callback chiamato in caso di errore
     */
    fun getCurrentLocation(
        context: Context,
        fusedLocationClient: FusedLocationProviderClient,
        onSuccess: (Location) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (!hasLocationPermission(context)) {
            onFailure(Exception("Location permission not granted"))
            return
        }

        val cancellationTokenSource = CancellationTokenSource()

        // @Suppress perché il controllo permessi è già fatto sopra,
        // ma il compilatore non riesce a tracciarlo
        @Suppress("MissingPermission")
        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            cancellationTokenSource.token
        ).addOnSuccessListener { location ->
            location?.let(onSuccess) ?: onFailure(Exception("Location is null"))
        }.addOnFailureListener(onFailure)
    }

    /**
     * Calcola la distanza tra due punti geografici.
     *
     * Internamente usa la formula di Haversine (implementata in Location.distanceBetween)
     * che tiene conto della curvatura terrestre per calcoli precisi.
     *
     * @return Distanza in CHILOMETRI (non metri!)
     */
    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0] / 1000  // Converto da metri a km
    }
}
