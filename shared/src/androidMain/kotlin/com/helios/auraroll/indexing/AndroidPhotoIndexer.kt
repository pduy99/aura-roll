package com.helios.auraroll.indexing

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.provider.MediaStore
import androidx.palette.graphics.Palette
import com.helios.auraroll.database.IndexedPhoto
import com.helios.auraroll.database.IndexedPhotoDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.yield

class AndroidPhotoIndexer(
    private val context: Context,
    private val dao: IndexedPhotoDao
) : PhotoIndexer {

    override fun index(): Flow<IndexingState> = flow {
        val resolver = context.contentResolver
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.SIZE
        )

        // Only query valid images
        val selection = "${MediaStore.Images.Media.SIZE} > 0"
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            sortOrder
        )?.use { cursor ->
            val totalImages = cursor.count
            if (totalImages == 0) {
                emit(IndexingState(progress = 1f, processedCount = 0, detectedPaletteColors = emptyList()))
                return@flow
            }

            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            var processed = 0
            val detectedColors = mutableSetOf<Long>()
            
            // Pre-fetch already indexed photo IDs from the database to speed up resumption
            val existingIds = dao.getAllIds().toSet()

            while (cursor.moveToNext()) {
                yield() // Allow cancellation
                val id = cursor.getLong(idColumn)
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                // Fast resume: skip images we have already processed
                if (existingIds.contains(id)) {
                    processed++
                    // Still emit progress periodically
                    if (processed % 100 == 0 || processed == totalImages) {
                        emit(
                            IndexingState(
                                progress = processed.toFloat() / totalImages,
                                processedCount = processed,
                                detectedPaletteColors = detectedColors.toList()
                            )
                        )
                    }
                    continue
                }

                try {
                    // Fast decode: load a highly downsampled thumbnail
                    var bitmap: Bitmap? = null
                    var calculatedSampleSize = 8
                    
                    resolver.openInputStream(contentUri)?.use { stream ->
                        val options = BitmapFactory.Options().apply {
                            inJustDecodeBounds = true
                        }
                        // We first need bounds
                        BitmapFactory.decodeStream(stream, null, options)
                        
                        // Calculate sample size to decode roughly ~50x50 bitmap for palette extraction
                        calculatedSampleSize = calculateInSampleSize(options, 50, 50)
                    }
                    
                    // Re-open stream to actually decode
                    resolver.openInputStream(contentUri)?.use { stream ->
                        val options = BitmapFactory.Options().apply {
                            inSampleSize = calculatedSampleSize
                        }
                        bitmap = BitmapFactory.decodeStream(stream, null, options)
                    }

                    if (bitmap != null) {
                        val palette = Palette.from(bitmap).generate()
                        
                        // Extract best color: prefer dominant, fallback to vibrant
                        val bestSwatch = palette.dominantSwatch ?: palette.vibrantSwatch ?: palette.swatches.maxByOrNull { it.population }
                        
                        if (bestSwatch != null) {
                            val argb = bestSwatch.rgb
                            val hsv = FloatArray(3)
                            Color.colorToHSV(argb, hsv)
                            
                            val hue = hsv[0]
                            val sat = hsv[1]
                            val brightness = hsv[2]
                            
                            // Monochrome detection per rules
                            val isMonochrome = sat < 0.1f || brightness < 0.1f
                            val storedHue = if (isMonochrome) null else hue
                            
                            val indexedPhoto = IndexedPhoto(
                                id = id,
                                uri = contentUri.toString(),
                                hue = storedHue,
                                saturation = sat,
                                brightness = brightness,
                                isMonochrome = isMonochrome,
                                dominantColorArgb = argb.toLong()
                            )
                            
                            // Store in DB
                            dao.insert(indexedPhoto)
                            
                            // Update sample set for the UI
                            if (!isMonochrome && detectedColors.size < 5) {
                                detectedColors.add(argb.toLong())
                            }
                        }
                        bitmap.recycle()
                    }
                } catch (t: Throwable) {
                    // Catch Throwable to prevent process death on OOM
                    t.printStackTrace()
                }

                processed++
                
                // Emit progress every 10 images or at the end to avoid flooding the flow
                if (processed % 10 == 0 || processed == totalImages) {
                    emit(
                        IndexingState(
                            progress = processed.toFloat() / totalImages,
                            processedCount = processed,
                            detectedPaletteColors = detectedColors.toList()
                        )
                    )
                }
            }
        } ?: run {
            // Null cursor case
            emit(IndexingState(progress = 1f, processedCount = 0, detectedPaletteColors = emptyList()))
        }
    }.flowOn(Dispatchers.IO)

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val (height: Int, width: Int) = options.outHeight to options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
}