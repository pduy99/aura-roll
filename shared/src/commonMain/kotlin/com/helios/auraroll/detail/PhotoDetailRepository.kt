package com.helios.auraroll.detail

/**
 * Loads on-demand, UI-ready [PhotoDetail] for a single photo by MediaStore id.
 *
 * Implementations are expected to combine indexed metadata (palette, hue) with
 * platform-specific extras such as filename, location, and EXIF. Errors are
 * surfaced via [Result] so the ViewModel can show a graceful empty state
 * instead of crashing on malformed images.
 */
interface PhotoDetailRepository {
    suspend fun loadDetail(photoId: Long): Result<PhotoDetail>
}
