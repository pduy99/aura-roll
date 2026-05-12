package com.helios.auraroll.detail.data

/**
 * Filename + raw GPS coordinates surfaced by the platform media store. Pure
 * data model — kept in commonMain so reverse-geocoding and orchestration logic
 * does not need to depend on platform types.
 */
data class PhotoMediaMeta(
    val fileName: String?,
    val latitude: Double?,
    val longitude: Double?,
)
