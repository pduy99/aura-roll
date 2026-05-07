package com.helios.auraroll.onboarding

/**
 * Static, in-memory tip catalogue. The first tip intentionally communicates the
 * offline-first indexing guarantee.
 */
class DefaultIndexingTipRepository : IndexingTipRepository {
    override fun tips(): List<IndexingTip> = TIPS

    private companion object {
        private val TIPS: List<IndexingTip> = listOf(
            IndexingTip("Aura Roll indexes everything offline first \u2014 your photos never leave the device."),
            IndexingTip("You can safely close the app; indexing will continue in the background."),
            IndexingTip("Each photo is analyzed for its dominant hues to power the spectrum view."),
            IndexingTip("Once indexing finishes, drag the spectrum slider to filter memories by color."),
        )
    }
}
