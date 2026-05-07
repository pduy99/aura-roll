package com.helios.auraroll.domain.usecase

import com.helios.auraroll.data.model.Photo
import com.helios.auraroll.data.repository.IndexedPhotoRepository
import com.helios.auraroll.hue.HUE_TOLERANCE_DEGREES
import com.helios.auraroll.hue.MIN_COLOR_DOMINANCE
import kotlinx.coroutines.flow.Flow

/**
 * Returns a live stream of photos matching the current filter mode.
 *
 * When [isMonochrome] is true the full monochrome set is returned.
 * Otherwise photos are filtered to those within [HUE_TOLERANCE_DEGREES] of [centerHue].
 */
class ObserveFilteredPhotosUseCase(
    private val repository: IndexedPhotoRepository
) {
    operator fun invoke(centerHue: Float, isMonochrome: Boolean): Flow<List<Photo>> =
        if (isMonochrome) repository.observeMonochromePhotos()
        else repository.observePhotosByHue(centerHue, HUE_TOLERANCE_DEGREES, MIN_COLOR_DOMINANCE)
}
