package com.helios.auraroll.quotes

import kotlin.random.Random

/**
 * Source of curator quotes keyed by editorial hue family names produced by
 * `com.helios.auraroll.hue.hueToColorName`.
 */
interface HueQuoteRepository {
    /** Returns the curated quotes for the given hue family, or a sensible fallback set. */
    fun quotesForHue(hueLabel: String): List<HueQuote>

    /** Picks a random quote for the given hue family. */
    fun randomQuoteFor(hueLabel: String, random: Random = Random.Default): HueQuote =
        quotesForHue(hueLabel).random(random)
}
