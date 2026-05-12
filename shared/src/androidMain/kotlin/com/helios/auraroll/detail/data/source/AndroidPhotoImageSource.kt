package com.helios.auraroll.detail.data.source

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.helios.auraroll.detail.data.RawPhotoImage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

/**
 * Android implementation of [PhotoImageSource]. Decodes a downsampled bitmap
 * via [BitmapFactory], then snapshots its pixels into a pure-Kotlin
 * [RawPhotoImage] so the rest of the pipeline stays platform-agnostic.
 */
class AndroidPhotoImageSource(
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : PhotoImageSource {

    override suspend fun loadDownsampled(uri: String, aspectRatio: Float): RawPhotoImage? =
        withContext(ioDispatcher) {
            val parsed = runCatching { Uri.parse(uri) }.getOrNull() ?: return@withContext null
            val resolver = context.contentResolver
            val bitmap = runCatching {
                val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                resolver.openInputStream(parsed)?.use {
                    BitmapFactory.decodeStream(it, null, bounds)
                }
                val srcW = bounds.outWidth.takeIf { it > 0 }
                    ?: (TARGET_PX * aspectRatio).roundToInt().coerceAtLeast(TARGET_PX)
                val srcH = bounds.outHeight.takeIf { it > 0 } ?: TARGET_PX
                val opts = BitmapFactory.Options().apply {
                    inSampleSize = calculateInSampleSize(srcW, srcH)
                    inPreferredConfig = Bitmap.Config.ARGB_8888
                }
                resolver.openInputStream(parsed)?.use {
                    BitmapFactory.decodeStream(it, null, opts)
                }
            }.getOrElse {
                Log.w(TAG, "Failed to decode bitmap for $uri", it)
                null
            } ?: return@withContext null

            try {
                val w = bitmap.width
                val h = bitmap.height
                val pixels = IntArray(w * h)
                bitmap.getPixels(pixels, 0, w, 0, 0, w, h)
                RawPhotoImage(width = w, height = h, pixels = pixels)
            } finally {
                bitmap.recycle()
            }
        }

    private fun calculateInSampleSize(srcWidth: Int, srcHeight: Int): Int {
        var sample = 1
        if (srcHeight > TARGET_PX || srcWidth > TARGET_PX) {
            val halfH = srcHeight / 2
            val halfW = srcWidth / 2
            while (halfH / sample >= TARGET_PX && halfW / sample >= TARGET_PX) {
                sample *= 2
            }
        }
        return sample
    }

    private companion object {
        private const val TAG = "PhotoImageSource"
        private const val TARGET_PX = 256
    }
}
