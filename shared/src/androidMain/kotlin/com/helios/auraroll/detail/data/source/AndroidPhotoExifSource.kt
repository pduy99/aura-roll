package com.helios.auraroll.detail.data.source

import android.content.Context
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import com.helios.auraroll.detail.PhotoExif
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

/**
 * Android implementation of [PhotoExifSource]. Reads camera-side EXIF tags
 * via [ExifInterface] and formats them for display. Returns an empty record
 * on any failure so the UI gracefully shows "—".
 */
class AndroidPhotoExifSource(
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : PhotoExifSource {

    override suspend fun readExif(uri: String): PhotoExif = withContext(ioDispatcher) {
        val parsed = runCatching { Uri.parse(uri) }.getOrNull() ?: return@withContext PhotoExif()
        runCatching {
            context.contentResolver.openInputStream(parsed)?.use { stream ->
                val exif = ExifInterface(stream)
                PhotoExif(
                    camera = formatCamera(exif),
                    lens = exif.getAttribute(ExifInterface.TAG_LENS_MODEL)
                        ?.takeIf { it.isNotBlank() },
                    aperture = formatAperture(exif),
                    exposure = formatExposure(exif),
                    iso = exif.getAttribute(ExifInterface.TAG_PHOTOGRAPHIC_SENSITIVITY)
                        ?: exif.getAttribute("ISOSpeedRatings"),
                    focalLength = formatFocalLength(exif),
                )
            }
        }.getOrNull() ?: PhotoExif()
    }

    private fun formatCamera(exif: ExifInterface): String? {
        val make = exif.getAttribute(ExifInterface.TAG_MAKE)?.trim().orEmpty()
        val model = exif.getAttribute(ExifInterface.TAG_MODEL)?.trim().orEmpty()
        if (make.isEmpty() && model.isEmpty()) return null
        return if (model.startsWith(make, ignoreCase = true)) model else "$make $model".trim()
    }

    private fun formatAperture(exif: ExifInterface): String? {
        val raw = exif.getAttribute(ExifInterface.TAG_F_NUMBER) ?: return null
        val f = raw.toFloatOrNull() ?: return null
        val pretty = if (f % 1f == 0f) f.toInt().toString() else "%.1f".format(f)
        return "ƒ/$pretty"
    }

    private fun formatExposure(exif: ExifInterface): String? {
        val raw = exif.getAttribute(ExifInterface.TAG_EXPOSURE_TIME) ?: return null
        val seconds = raw.toFloatOrNull() ?: return null
        return when {
            seconds >= 1f -> "${seconds.roundToInt()}s"
            seconds > 0f -> "1/${(1f / seconds).roundToInt()}s"
            else -> null
        }
    }

    private fun formatFocalLength(exif: ExifInterface): String? {
        val raw = exif.getAttribute(ExifInterface.TAG_FOCAL_LENGTH) ?: return null
        val parts = raw.split("/")
        val mm = when (parts.size) {
            2 -> {
                val num = parts[0].toFloatOrNull() ?: return null
                val den = parts[1].toFloatOrNull()?.takeIf { it != 0f } ?: return null
                num / den
            }
            else -> raw.toFloatOrNull() ?: return null
        }
        return "${mm.roundToInt()}mm"
    }
}
