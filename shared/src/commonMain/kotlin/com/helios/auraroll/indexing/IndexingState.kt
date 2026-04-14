package com.helios.auraroll.indexing

data class IndexingState(
    val progress: Float = 0f,
    val processedCount: Int = 0,
    val detectedPaletteColors: List<Long> = emptyList() // ARGB packed longs representing colors
)