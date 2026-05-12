package com.helios.auraroll.detail.data.source

import android.graphics.Bitmap
import androidx.palette.graphics.Palette
import com.helios.auraroll.detail.PaletteSwatch
import com.helios.auraroll.detail.data.RawPhotoImage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Android implementation of [PaletteExtractorSource] backed by androidx.palette.
 * Reconstructs a [Bitmap] from the platform-agnostic [RawPhotoImage] pixels so
 * the rest of the pipeline does not need to know about Android types.
 */
class AndroidPaletteExtractorSource(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.Default,
) : PaletteExtractorSource {

    override suspend fun extract(image: RawPhotoImage, maxSwatches: Int): List<PaletteSwatch> =
        withContext(ioDispatcher) {
            if (image.width <= 0 || image.height <= 0) return@withContext emptyList()
            val bitmap = Bitmap.createBitmap(
                image.pixels,
                image.width,
                image.height,
                Bitmap.Config.ARGB_8888,
            )
            try {
                val palette = Palette.Builder(bitmap)
                    .resizeBitmapArea(-1)
                    .maximumColorCount(PALETTE_MAX_COLORS)
                    .clearFilters()
                    .generate()
                val swatches = palette.swatches.sortedByDescending { it.population }
                if (swatches.isEmpty()) return@withContext emptyList()
                val totalPop = swatches.sumOf { it.population }.coerceAtLeast(1)
                swatches.take(maxSwatches).map { sw ->
                    PaletteSwatch(
                        argb = (0xFF000000.toInt() or (sw.rgb and 0x00FFFFFF))
                            .toLong() and 0xFFFFFFFFL,
                        hex = "#%06X".format(sw.rgb and 0x00FFFFFF),
                        population = sw.population.toFloat() / totalPop.toFloat(),
                    )
                }
            } finally {
                bitmap.recycle()
            }
        }

    private companion object {
        private const val PALETTE_MAX_COLORS = 24
    }
}
