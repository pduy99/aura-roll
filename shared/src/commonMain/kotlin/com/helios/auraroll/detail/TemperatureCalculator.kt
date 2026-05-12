package com.helios.auraroll.detail

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt

/**
 * Maps a list of palette swatches to a signed cool/warm score in `[-1f, 1f]`.
 *
 * Each swatch contributes `cos(hue - 30°) * saturation * population`:
 * - hues around 30° (warm orange) push positive, around 210° (cool blue) push negative
 * - low-saturation swatches barely move the needle
 * - population weights dominant colors more
 *
 * The result is normalized by the population sum so it is independent of
 * absolute swatch counts. Pure Kotlin — safe to reuse on iOS and in tests.
 */
object TemperatureCalculator {

    private const val WARM_AXIS_DEG = 30.0

    fun compute(samples: List<TemperatureSample>): Float {
        if (samples.isEmpty()) return 0f
        var totalWeight = 0.0
        var signed = 0.0
        for (sample in samples) {
            val weight = sample.saturation.coerceIn(0f, 1f) * sample.population.coerceAtLeast(0f)
            if (weight <= 0f) continue
            val angleRad = (sample.hue - WARM_AXIS_DEG) * PI / 180.0
            signed += cos(angleRad) * weight
            totalWeight += weight
        }
        if (totalWeight <= 0.0) return 0f
        return (signed / totalWeight).toFloat().coerceIn(-1f, 1f)
    }

    /** Renders a value from [compute] as `"-12% Cool"` / `"+8% Warm"` / `"Neutral"`. */
    fun formatLabel(temperature: Float): String {
        val pct = (temperature * 100f).roundToInt()
        return when {
            pct == 0 -> "Neutral"
            pct < 0 -> "${pct}% Cool"
            else -> "+${pct}% Warm"
        }
    }
}

data class TemperatureSample(
    val hue: Float,
    val saturation: Float,
    val population: Float,
)
