package com.helios.auraroll.detail.data

/**
 * A platform-agnostic, downsampled snapshot of a photo's pixels.
 *
 * `pixels` is laid out in row-major order, packed ARGB, length `width * height`.
 * Keeping this pure-Kotlin lets the histogram + hue analysis run on iOS without
 * pulling in [android.graphics.Bitmap].
 */
data class RawPhotoImage(
    val width: Int,
    val height: Int,
    val pixels: IntArray,
) {
    override fun equals(other: Any?): Boolean = this === other
    override fun hashCode(): Int = width * 31 + height
}
