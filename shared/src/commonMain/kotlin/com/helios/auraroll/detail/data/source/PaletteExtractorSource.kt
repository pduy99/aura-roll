package com.helios.auraroll.detail.data.source

import com.helios.auraroll.detail.PaletteSwatch
import com.helios.auraroll.detail.data.RawPhotoImage

/**
 * Extracts an editorial palette (top N most-populous swatches) from a
 * downsampled image. Backed on Android by androidx.palette; iOS will hook
 * into Vision/CoreImage.
 */
interface PaletteExtractorSource {
    suspend fun extract(image: RawPhotoImage, maxSwatches: Int): List<PaletteSwatch>
}
