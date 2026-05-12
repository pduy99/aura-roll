package com.helios.auraroll.detail.data.source

import com.helios.auraroll.detail.data.RawPhotoImage

/**
 * Loads a downsampled, pixel-accessible snapshot of a photo's bytes. The
 * `aspectRatio` hint helps the implementation pick a sensible target size
 * without having to first decode the full bounds when EXIF is unavailable.
 */
interface PhotoImageSource {
    suspend fun loadDownsampled(uri: String, aspectRatio: Float): RawPhotoImage?
}
