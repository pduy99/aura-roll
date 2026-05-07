package com.helios.auraroll.home.impl.utils

import androidx.compose.ui.graphics.Color

/**
 * Converts a hue angle to a vibrant Compose [Color] for the background tint animation.
 *
 * Uses Compose's KMP-friendly [Color.hsv] so the helper has no `android.graphics` dependency.
 * Business logic (color naming, tolerance) lives in the shared module instead.
 */
fun hueToColor(hue: Float): Color {
    val normalized = ((hue % 360f) + 360f) % 360f
    return Color.hsv(hue = normalized, saturation = 0.75f, value = 1f)
}
