package com.helios.auraroll.data.repository

import com.helios.auraroll.data.model.Photo
import kotlinx.coroutines.flow.Flow

interface IndexedPhotoRepository {
    fun observeProcessedCount(): Flow<Int>
    fun observeSampleColors(): Flow<List<Long>>

    /**
     * Returns photos whose hue falls within [centerHue ± toleranceDeg] (wrapping at 360°)
     * and whose winning swatch covers at least [minDominance] (0–1) of the palette population.
     */
    fun observePhotosByHue(centerHue: Float, toleranceDeg: Float, minDominance: Float): Flow<List<Photo>>

    /** Returns the count of photos in the given hue range, wrapping at 360°, filtered by dominance. */
    fun observePhotoCountByHue(centerHue: Float, toleranceDeg: Float, minDominance: Float): Flow<Int>

    fun observeMonochromePhotos(): Flow<List<Photo>>
    fun observeMonochromePhotoCount(): Flow<Int>
}
