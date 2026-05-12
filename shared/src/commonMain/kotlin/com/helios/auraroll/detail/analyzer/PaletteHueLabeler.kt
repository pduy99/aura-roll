package com.helios.auraroll.detail.analyzer

import com.helios.auraroll.database.IndexedPhoto
import com.helios.auraroll.detail.PaletteSwatch
import com.helios.auraroll.detail.TemperatureSample
import com.helios.auraroll.hue.hueToColorName

/**
 * Pure helpers that translate a palette into the editorial dominant/accent
 * hue labels and into the [TemperatureSample]s consumed by
 * [com.helios.auraroll.detail.TemperatureCalculator].
 */
internal object PaletteHueLabeler {

    fun labels(palette: List<PaletteSwatch>, fallback: IndexedPhoto): Pair<String, String?> {
        val hueLabels = palette.mapNotNull { swatch ->
            val hsv = argbToHsv(swatch.argb.toInt())
            if (hsv.saturation < 0.15f || hsv.value < 0.1f) null else hueToColorName(hsv.hue)
        }
        val dominant = hueLabels.firstOrNull()
            ?: fallback.hue?.let(::hueToColorName)
            ?: "Monochrome"
        val accent = hueLabels.drop(1).firstOrNull { it != dominant }
        return dominant to accent
    }

    fun toTemperatureSamples(palette: List<PaletteSwatch>): List<TemperatureSample> =
        palette.map { swatch ->
            val hsv = argbToHsv(swatch.argb.toInt())
            TemperatureSample(
                hue = hsv.hue,
                saturation = hsv.saturation,
                population = swatch.population,
            )
        }

    fun fallbackPalette(indexed: IndexedPhoto): List<PaletteSwatch> {
        val argb = indexed.dominantColorArgb.toInt()
        val rgb = argb and 0x00FFFFFF
        return listOf(
            PaletteSwatch(
                argb = (0xFF000000.toInt() or rgb).toLong() and 0xFFFFFFFFL,
                hex = formatHex(rgb),
                population = 1f,
            )
        )
    }

    private fun formatHex(rgb: Int): String {
        val hex = (rgb and 0x00FFFFFF).toString(16).uppercase().padStart(6, '0')
        return "#$hex"
    }
}
