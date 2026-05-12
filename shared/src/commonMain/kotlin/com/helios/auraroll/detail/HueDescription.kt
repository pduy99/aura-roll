package com.helios.auraroll.detail

/**
 * Builds the editorial sentence shown under the palette swatches.
 *
 * Pattern: _"Our neural engine has identified a dominant <X> palette with grounding
 * <Y> undertones. The composition leans heavily into the <cool|warm> spectrum,
 * suggesting a <mood> mood."_
 */
object HueDescription {

    fun build(
        dominant: String,
        accent: String?,
        temperature: Float,
    ): String {
        val spectrum = when {
            temperature <= -0.05f -> "cool"
            temperature >= 0.05f -> "warm"
            else -> "balanced"
        }
        val mood = when {
            temperature <= -0.25f -> "tranquil and expansive"
            temperature <= -0.05f -> "calm and contemplative"
            temperature in -0.05f..0.05f -> "centered and natural"
            temperature < 0.25f -> "lively and inviting"
            else -> "energetic and radiant"
        }
        val opener = if (accent != null) {
            "Our neural engine has identified a dominant $dominant palette with grounding $accent undertones."
        } else {
            "Our neural engine has identified a dominant $dominant palette."
        }
        val closer = "The composition leans heavily into the $spectrum spectrum, suggesting a $mood mood."
        return "$opener $closer"
    }
}
