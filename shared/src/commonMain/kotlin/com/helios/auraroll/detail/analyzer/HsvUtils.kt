package com.helios.auraroll.detail.analyzer

import kotlin.math.max
import kotlin.math.min

/**
 * Pure HSV decomposition for a packed ARGB color.
 *
 * `hue` is in `[0f, 360f)`, `saturation` and `value` in `[0f, 1f]`. Mirrors
 * the contract of `android.graphics.Color.colorToHSV` so the rest of the
 * pipeline does not care about the platform.
 */
internal data class Hsv(val hue: Float, val saturation: Float, val value: Float)

internal fun argbToHsv(argb: Int): Hsv {
    val r = ((argb shr 16) and 0xFF) / 255f
    val g = ((argb shr 8) and 0xFF) / 255f
    val b = (argb and 0xFF) / 255f
    val maxC = max(r, max(g, b))
    val minC = min(r, min(g, b))
    val delta = maxC - minC
    val hue = when {
        delta == 0f -> 0f
        maxC == r -> 60f * (((g - b) / delta) % 6f)
        maxC == g -> 60f * (((b - r) / delta) + 2f)
        else -> 60f * (((r - g) / delta) + 4f)
    }.let { if (it < 0f) it + 360f else it }
    val saturation = if (maxC == 0f) 0f else delta / maxC
    return Hsv(hue, saturation, maxC)
}
