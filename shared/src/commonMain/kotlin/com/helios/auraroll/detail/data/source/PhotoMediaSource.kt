package com.helios.auraroll.detail.data.source

import com.helios.auraroll.detail.data.PhotoMediaMeta

/**
 * Bridges the app to the platform's media catalogue (MediaStore on Android,
 * PHAsset on iOS). Returns `null` if the photo is unknown to the system.
 */
interface PhotoMediaSource {
    suspend fun queryMeta(photoId: Long): PhotoMediaMeta?
}
