package com.helios.auraroll.onboarding

/**
 * Source of indexing tips shown (rotating) on the onboarding indexing screen.
 * Lives in `:shared` so iOS can reuse the same copy.
 */
interface IndexingTipRepository {
    /** Returns the curated tips in display order. */
    fun tips(): List<IndexingTip>
}
