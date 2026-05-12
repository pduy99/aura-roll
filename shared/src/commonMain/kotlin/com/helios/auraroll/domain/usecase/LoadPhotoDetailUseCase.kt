package com.helios.auraroll.domain.usecase

import com.helios.auraroll.detail.PhotoDetail
import com.helios.auraroll.detail.PhotoDetailRepository

/**
 * Use case that returns the full editorial detail for a single photo, including
 * palette, EXIF, and histogram. Pure delegation today; gives the ViewModel a
 * stable seam for caching or instrumentation later without touching consumers.
 */
class LoadPhotoDetailUseCase(
    private val repository: PhotoDetailRepository,
) {
    suspend operator fun invoke(photoId: Long): Result<PhotoDetail> =
        repository.loadDetail(photoId)
}
