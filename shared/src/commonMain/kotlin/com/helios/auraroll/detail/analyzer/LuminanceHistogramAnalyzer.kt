package com.helios.auraroll.detail.analyzer

import com.helios.auraroll.detail.LuminanceHistogram
import com.helios.auraroll.detail.PaletteSwatch
import com.helios.auraroll.detail.data.RawPhotoImage
import kotlin.math.abs

/**
 * Builds a 5-bin luminance histogram from a downsampled image, tinting each
 * bar with the palette swatch whose hue best matches that bin's average hue.
 *
 * Pure Kotlin — runs on every KMP target.
 */
internal object LuminanceHistogramAnalyzer {

    private const val BIN_COUNT = 5
    private const val SAMPLE_DIM = 64
    private const val DEFAULT_TINT = 0xFF1F1F1FL

    fun analyze(image: RawPhotoImage, palette: List<PaletteSwatch>): LuminanceHistogram {
        val w = image.width
        val h = image.height
        if (w <= 0 || h <= 0) return empty()
        val stepX = (w / SAMPLE_DIM).coerceAtLeast(1)
        val stepY = (h / SAMPLE_DIM).coerceAtLeast(1)

        val bins = IntArray(BIN_COUNT)
        val hueSum = FloatArray(BIN_COUNT)
        val hueCount = IntArray(BIN_COUNT)

        var y = 0
        while (y < h) {
            var x = 0
            val rowStart = y * w
            while (x < w) {
                val hsv = argbToHsv(image.pixels[rowStart + x])
                val binIdx = (hsv.value * BIN_COUNT).toInt().coerceIn(0, BIN_COUNT - 1)
                bins[binIdx]++
                if (hsv.saturation > 0.15f) {
                    hueSum[binIdx] += hsv.hue
                    hueCount[binIdx]++
                }
                x += stepX
            }
            y += stepY
        }

        val maxBin = bins.maxOrNull()?.toFloat()?.coerceAtLeast(1f) ?: 1f
        val normalized = bins.map { it / maxBin }
        val tints = bins.indices.map { idx ->
            val avgHue = if (hueCount[idx] > 0) hueSum[idx] / hueCount[idx] else null
            pickPaletteColor(palette, avgHue, idx)
        }
        val peakBin = bins.indices.maxByOrNull { bins[it] } ?: 2
        return LuminanceHistogram(bins = normalized, tintArgb = tints, peakBin = peakBin)
    }

    fun empty(): LuminanceHistogram = LuminanceHistogram(
        bins = List(BIN_COUNT) { 0f },
        tintArgb = List(BIN_COUNT) { DEFAULT_TINT },
        peakBin = 2,
    )

    private fun pickPaletteColor(palette: List<PaletteSwatch>, hue: Float?, fallbackIdx: Int): Long {
        if (palette.isEmpty()) return DEFAULT_TINT
        if (hue == null) {
            return palette[fallbackIdx.coerceIn(0, palette.lastIndex)].argb
        }
        var bestIdx = 0
        var bestDist = Float.MAX_VALUE
        val target = ((hue % 360f) + 360f) % 360f
        for ((i, swatch) in palette.withIndex()) {
            val swatchHue = argbToHsv(swatch.argb.toInt()).hue
            val diff = abs(swatchHue - target).let { if (it > 180f) 360f - it else it }
            if (diff < bestDist) {
                bestDist = diff
                bestIdx = i
            }
        }
        return palette[bestIdx].argb
    }
}
