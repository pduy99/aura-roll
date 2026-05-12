package com.helios.auraroll.detail.data.source

import android.content.Context
import android.location.Geocoder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

/**
 * Android implementation of [PhotoLocationSource] using the system [Geocoder].
 * Falls back to `null` when the device has no geocoding provider.
 */
class AndroidPhotoLocationSource(
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : PhotoLocationSource {

    override suspend fun reverseGeocode(latitude: Double, longitude: Double): String? =
        withContext(ioDispatcher) {
            if (!Geocoder.isPresent()) return@withContext null
            runCatching {
                @Suppress("DEPRECATION")
                val results = Geocoder(context, Locale.getDefault())
                    .getFromLocation(latitude, longitude, 1)
                val first = results?.firstOrNull() ?: return@runCatching null
                val city = first.locality ?: first.subAdminArea ?: first.adminArea
                val region = first.adminArea ?: first.countryName
                when {
                    city != null && region != null && !city.equals(region, ignoreCase = true) ->
                        "SHOT IN ${city.uppercase()}, ${region.uppercase()}"
                    city != null -> "SHOT IN ${city.uppercase()}"
                    region != null -> "SHOT IN ${region.uppercase()}"
                    else -> null
                }
            }.getOrNull()
        }
}
