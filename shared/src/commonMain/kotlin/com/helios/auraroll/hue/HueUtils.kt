package com.helios.auraroll.hue

/** Hue filter tolerance in degrees — defines the ±arc around the selected hue shown in the gallery. */
const val HUE_TOLERANCE_DEGREES = 30f

/**
 * Minimum share of the palette population that must be covered by swatches whose hue is within
 * [HUE_TOLERANCE_DEGREES] of the photo's primary hue. Photos below this threshold are hidden from
 * hue-filtered results so the gallery only shows images where the selected color is genuinely present.
 */
const val MIN_COLOR_DOMINANCE = 0.45f

/**
 * Returns the smallest angular distance between two hues on the 0–360° color wheel.
 * Wraps around so e.g. `circularHueDistance(350f, 10f) == 20f`.
 */
fun circularHueDistance(a: Float, b: Float): Float {
    val diff = ((a - b) % 360f + 360f) % 360f
    return if (diff > 180f) 360f - diff else diff
}

/**
 * Maps a hue angle (0–360°) to an editorial color name fitting the
 * "Living Curator" aesthetic of AuraRoll.
 *
 * Pure Kotlin — safe for all KMP targets.
 */
fun hueToColorName(hue: Float): String {
    val normalized = ((hue % 360f) + 360f) % 360f
    return when {
        normalized !in 15f..<345f -> "Volcanic Red"
        normalized < 45f -> "Desert Amber"
        normalized < 75f -> "Solar Gold"
        normalized < 105f -> "Garden Lime"
        normalized < 135f -> "Forest Emerald"
        normalized < 165f -> "Jade Mist"
        normalized < 195f -> "Arctic Teal"
        normalized < 225f -> "Oceanic Cyan"
        normalized < 255f -> "Sapphire Blue"
        normalized < 285f -> "Twilight Violet"
        normalized < 315f -> "Amethyst Purple"
        else -> "Rose Dusk"
    }
}
