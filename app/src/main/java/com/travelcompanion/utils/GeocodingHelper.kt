package com.travelcompanion.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException
import java.util.Locale
import kotlin.coroutines.resume

/**
 * Helper class for geocoding operations.
 * Converts location names to coordinates (latitude/longitude).
 */
object GeocodingHelper {

    data class GeocodingResult(
        val latitude: Double,
        val longitude: Double,
        val formattedAddress: String? = null
    )

    /**
     * Converts a location name (e.g., "Rome, Italy") to coordinates.
     * Uses Android's built-in Geocoder.
     *
     * @param context Android context
     * @param locationName The location name to geocode
     * @return GeocodingResult with coordinates, or null if geocoding fails
     */
    suspend fun geocodeLocation(context: Context, locationName: String): GeocodingResult? {
        if (locationName.isBlank()) return null

        return withContext(Dispatchers.IO) {
            try {
                if (!Geocoder.isPresent()) {
                    Timber.w("Geocoder is not available on this device")
                    return@withContext null
                }

                val geocoder = Geocoder(context, Locale.getDefault())

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    // Use the new async API for Android 13+
                    suspendCancellableCoroutine { continuation ->
                        geocoder.getFromLocationName(locationName, 1) { addresses ->
                            val address = addresses.firstOrNull()
                            if (address != null && address.hasLatitude() && address.hasLongitude()) {
                                continuation.resume(
                                    GeocodingResult(
                                        latitude = address.latitude,
                                        longitude = address.longitude,
                                        formattedAddress = formatAddress(address)
                                    )
                                )
                            } else {
                                continuation.resume(null)
                            }
                        }
                    }
                } else {
                    // Use the synchronous API for older Android versions
                    @Suppress("DEPRECATION")
                    val addresses = geocoder.getFromLocationName(locationName, 1)
                    val address = addresses?.firstOrNull()
                    if (address != null && address.hasLatitude() && address.hasLongitude()) {
                        GeocodingResult(
                            latitude = address.latitude,
                            longitude = address.longitude,
                            formattedAddress = formatAddress(address)
                        )
                    } else {
                        null
                    }
                }
            } catch (e: IOException) {
                Timber.e(e, "Geocoding failed for: $locationName")
                null
            } catch (e: Exception) {
                Timber.e(e, "Unexpected error during geocoding for: $locationName")
                null
            }
        }
    }

    private fun formatAddress(address: Address): String {
        return buildString {
            address.thoroughfare?.let { append(it) }
            address.locality?.let {
                if (isNotEmpty()) append(", ")
                append(it)
            }
            address.countryName?.let {
                if (isNotEmpty()) append(", ")
                append(it)
            }
        }.ifEmpty { address.getAddressLine(0) ?: "" }
    }
}
