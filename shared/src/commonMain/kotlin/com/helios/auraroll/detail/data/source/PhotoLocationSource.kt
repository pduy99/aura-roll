package com.helios.auraroll.detail.data.source

/**
 * Reverse-geocodes raw lat/lng into an editorial "SHOT IN <CITY>, <REGION>"
 * string. Implementations may hit the system geocoder, a remote API, or be a
 * no-op when no provider is available.
 */
interface PhotoLocationSource {
    suspend fun reverseGeocode(latitude: Double, longitude: Double): String?
}
