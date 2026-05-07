package com.helios.auraroll.quotes

import kotlin.random.Random

/**
 * Pure-Kotlin helpers for choosing a curator quote and where to drop it into
 * the photo grid. Lives in `:shared` so iOS can reuse the same logic.
 */
class SelectHueQuoteUseCase(
    private val repository: HueQuoteRepository,
    private val random: Random = Random.Default,
) {
    /** Picks a random quote for the given hue family. */
    fun pickQuote(hueLabel: String): HueQuote =
        repository.randomQuoteFor(hueLabel, random)

    /**
     * Picks a random insertion index near the top of the photo list so the quote card is
     * reachable within a couple of scrolls (roughly 2-4 rows down in a 2-column grid).
     * Returns [NO_INSERT] when the list is too short to host a quote without crowding.
     * Reuses [previous] when it still falls in range to keep the card visually stable
     * across small list size changes.
     */
    fun insertIndex(size: Int, previous: Int = NO_INSERT): Int {
        if (size < QUOTE_MIN_LIST_SIZE) return NO_INSERT
        val lower = QUOTE_INSERT_LOWER_BOUND
        val upper = (size - 1).coerceAtMost(QUOTE_INSERT_UPPER_BOUND_EXCLUSIVE)
        if (upper <= lower) return lower
        if (previous in lower until upper) return previous
        return random.nextInt(lower, upper)
    }

    companion object {
        const val NO_INSERT: Int = -1
        private const val QUOTE_MIN_LIST_SIZE = 5
        private const val QUOTE_INSERT_LOWER_BOUND = 4
        private const val QUOTE_INSERT_UPPER_BOUND_EXCLUSIVE = 9
    }
}
