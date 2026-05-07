package com.helios.auraroll.domain.usecase

import com.helios.auraroll.data.repository.IndexedPhotoRepository
import com.helios.auraroll.hue.HUE_TOLERANCE_DEGREES
import com.helios.auraroll.hue.MIN_COLOR_DOMINANCE
import kotlinx.coroutines.flow.Flow

/**
 * Returns a live count of photos matching the current filter mode.
 *
 * When [isMonochrome] is true the total monochrome count is returned.
 * Otherwise the count of photos within [HUE_TOLERANCE_DEGREES] of [centerHue] is returned.
 */
class ObservePhotoCountUseCase(
    private val repository: IndexedPhotoRepository
) {
    operator fun invoke(centerHue: Float, isMonochrome: Boolean): Flow<Int> =
        if (isMonochrome) repository.observeMonochromePhotoCount()
        else repository.observePhotoCountByHue(centerHue, HUE_TOLERANCE_DEGREES, MIN_COLOR_DOMINANCE)
}
