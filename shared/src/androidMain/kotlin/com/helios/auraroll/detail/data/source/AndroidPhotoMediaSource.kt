package com.helios.auraroll.detail.data.source

import android.content.Context
import android.provider.MediaStore
import com.helios.auraroll.detail.data.PhotoMediaMeta
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Android implementation of [PhotoMediaSource] backed by the system MediaStore.
 * Owns the only `ContentResolver` access for media catalogue lookups.
 */
class AndroidPhotoMediaSource(
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : PhotoMediaSource {

    override suspend fun queryMeta(photoId: Long): PhotoMediaMeta? = withContext(ioDispatcher) {
        runCatching {
            context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                PROJECTION,
                "${MediaStore.Images.Media._ID} = ?",
                arrayOf(photoId.toString()),
                null,
            )?.use { cursor ->
                if (!cursor.moveToFirst()) return@use null
                val name = cursor.getString(0)
                val lat = cursor.getDouble(1)
                val lng = cursor.getDouble(2)
                val hasCoords = lat != 0.0 || lng != 0.0
                PhotoMediaMeta(
                    fileName = name,
                    latitude = if (hasCoords) lat else null,
                    longitude = if (hasCoords) lng else null,
                )
            }
        }.getOrNull()
    }

    private companion object {
        private val PROJECTION = arrayOf(
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.LATITUDE,
            MediaStore.Images.Media.LONGITUDE,
        )
    }
}
