package com.helios.auraroll.indexing

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.provider.MediaStore
import android.util.Log
import androidx.palette.graphics.Palette
import com.helios.auraroll.database.IndexedPhoto
import com.helios.auraroll.database.IndexedPhotoDao
import com.helios.auraroll.hue.HUE_TOLERANCE_DEGREES
import com.helios.auraroll.hue.circularHueDistance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class AndroidPhotoIndexer(
    private val context: Context,
    private val dao: IndexedPhotoDao,
) : PhotoIndexer {

    private data class RowMeta(
        val id: Long,
        val width: Int,
        val height: Int,
    )

    override fun index(): Flow<IndexingState> = flow {
        val rows: List<RowMeta>
        val totalImages: Int
        val alreadyIndexed: Int

        val existingIds = dao.getAllIds().toHashSet()

        val resolver = context.contentResolver
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT,
        )
        val selection = "${MediaStore.Images.Media.SIZE} > 0"
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            sortOrder,
        ).use { cursor ->
            if (cursor == null) {
                emit(IndexingState(progress = 1f, processedCount = 0))
                return@flow
            }
            totalImages = cursor.count
            if (totalImages == 0) {
                emit(IndexingState(progress = 1f, processedCount = 0))
                return@flow
            }
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)
            val heightColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)
            val pending = ArrayList<RowMeta>(totalImages)
            var existingCount = 0
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                if (id in existingIds) {
                    existingCount++
                    continue
                }
                pending += RowMeta(
                    id = id,
                    width = cursor.getInt(widthColumn),
                    height = cursor.getInt(heightColumn),
                )
            }
            rows = pending
            alreadyIndexed = existingCount
        }

        if (rows.isEmpty()) {
            emit(IndexingState(progress = 1f, processedCount = totalImages))
            return@flow
        }

        runIndexingPipeline(
            rows = rows,
            totalImages = totalImages,
            alreadyIndexed = alreadyIndexed,
            collector = this,
        )
    }.flowOn(Dispatchers.IO)

    /**
     * Producer-consumer pipeline:
     *  - one producer pushes RowMeta items into a work channel
     *  - N workers (bound to a worker dispatcher) decode + analyze and push results into a result channel
     *  - the calling coroutine (this Flow's collector) drains the result channel, batching DB writes
     *    and emitting progress lock-free since it is the single consumer.
     *
     * Workers run on `Dispatchers.IO.limitedParallelism` so blocking JPEG decode/disk reads do not
     * starve the shared CPU dispatcher used elsewhere in the app.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun runIndexingPipeline(
        rows: List<RowMeta>,
        totalImages: Int,
        alreadyIndexed: Int,
        collector: FlowCollector<IndexingState>,
    ) = coroutineScope {
        val cores = Runtime.getRuntime().availableProcessors().coerceAtLeast(2)
        // JPEG decode is mixed I/O + CPU; allow modest oversubscription to hide stream stalls.
        val workerCount = (cores * 2).coerceAtMost(MAX_WORKERS)
        val workerDispatcher = Dispatchers.IO.limitedParallelism(workerCount)

        val workQueue = Channel<RowMeta>(capacity = workerCount * 2)
        val resultQueue = Channel<IndexedPhoto?>(capacity = workerCount * 2)

        // Producer
        launch(Dispatchers.Default) {
            try {
                for (row in rows) workQueue.send(row)
            } finally {
                workQueue.close()
            }
        }

        // Workers
        val workers = List(workerCount) {
            async(workerDispatcher) {
                for (row in workQueue) {
                    val photo = analyzeRow(row)
                    resultQueue.send(photo)
                }
            }
        }

        // Closer: shut the result channel after all workers complete.
        launch(Dispatchers.Default) {
            workers.awaitAll()
            resultQueue.close()
        }

        // Consumer (this coroutine): single-threaded drain → batch insert → progress emit.
        var processed = alreadyIndexed
        var sinceLastEmit = 0
        val buffer = ArrayList<IndexedPhoto>(BATCH_SIZE)

        for (photo in resultQueue) {
            if (photo != null) {
                buffer += photo
                if (buffer.size >= BATCH_SIZE) {
                    dao.insertAll(buffer)
                    buffer.clear()
                }
            }
            processed++
            sinceLastEmit++
            if (sinceLastEmit >= PROGRESS_EMIT_EVERY || processed == totalImages) {
                sinceLastEmit = 0
                collector.emit(
                    IndexingState(
                        progress = processed.toFloat() / totalImages,
                        processedCount = processed,
                    )
                )
            }
        }

        if (buffer.isNotEmpty()) {
            dao.insertAll(buffer)
            buffer.clear()
        }
        collector.emit(
            IndexingState(
                progress = 1f,
                processedCount = processed,
            )
        )
    }

    private fun analyzeRow(row: RowMeta): IndexedPhoto? {
        val resolver = context.contentResolver
        val contentUri = ContentUris.withAppendedId(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            row.id,
        )
        return try {
            val hasValidCursorDims = row.width > 0 && row.height > 0
            var aspectRatio = if (hasValidCursorDims) {
                row.width.toFloat() / row.height.toFloat()
            } else 1f

            val sampleSize: Int = if (hasValidCursorDims) {
                calculateInSampleSize(row.width, row.height)
            } else {
                var s = 8
                resolver.openInputStream(contentUri)?.use { stream ->
                    val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                    BitmapFactory.decodeStream(stream, null, options)
                    if (options.outWidth > 0 && options.outHeight > 0) {
                        aspectRatio = options.outWidth.toFloat() / options.outHeight.toFloat()
                        s = calculateInSampleSize(options.outWidth, options.outHeight)
                    }
                }
                s
            }

            val bitmap: Bitmap = resolver.openInputStream(contentUri)?.use { stream ->
                val options = BitmapFactory.Options().apply {
                    inSampleSize = sampleSize
                    // RGB_565 halves memory and decode cost; precision is far inside the
                    // 30-degree hue tolerance used downstream.
                    inPreferredConfig = Bitmap.Config.RGB_565
                }
                BitmapFactory.decodeStream(stream, null, options)
            } ?: return null

            val palette = try {
                Palette.Builder(bitmap)
                    .resizeBitmapArea(-1) // already downsampled; skip Palette's internal resize
                    .maximumColorCount(PALETTE_MAX_COLORS)
                    .clearFilters()
                    .generate()
            } finally {
                bitmap.recycle()
            }

            val swatches = palette.swatches
            val n = swatches.size
            if (n == 0) return null

            val hues = FloatArray(n)
            val sats = FloatArray(n)
            val vals = FloatArray(n)
            val pops = IntArray(n)
            val tmp = FloatArray(3)
            var totalPopulation = 0
            var bestIdx = -1
            var bestWeight = -1f
            var fallbackIdx = 0
            var fallbackPop = -1

            for (i in 0 until n) {
                val sw = swatches[i]
                Color.colorToHSV(sw.rgb, tmp)
                val s = tmp[1]
                val v = tmp[2]
                hues[i] = tmp[0]
                sats[i] = s
                vals[i] = v
                val pop = sw.population
                pops[i] = pop
                totalPopulation += pop
                val weight = if (s < SAT_THRESHOLD || v < VAL_THRESHOLD) 0f else s * pop
                if (weight > bestWeight) {
                    bestWeight = weight
                    bestIdx = i
                }
                if (pop > fallbackPop) {
                    fallbackPop = pop
                    fallbackIdx = i
                }
            }

            val chosenIdx = if (bestWeight > 0f) bestIdx else {
                val dominant = palette.dominantSwatch
                val dominantIdx = if (dominant != null) swatches.indexOf(dominant) else -1
                if (dominantIdx >= 0) dominantIdx else fallbackIdx
            }

            val hue = hues[chosenIdx]
            val sat = sats[chosenIdx]
            val brightness = vals[chosenIdx]
            val isMonochrome = sat < SAT_THRESHOLD || brightness < MONO_VAL_THRESHOLD

            val matchingPopulation = if (isMonochrome) {
                pops[chosenIdx]
            } else {
                var sum = 0
                for (i in 0 until n) {
                    if (sats[i] >= SAT_THRESHOLD &&
                        vals[i] >= VAL_THRESHOLD &&
                        circularHueDistance(hues[i], hue) <= HUE_TOLERANCE_DEGREES
                    ) {
                        sum += pops[i]
                    }
                }
                sum
            }
            val colorDominance = if (totalPopulation > 0) {
                matchingPopulation.toFloat() / totalPopulation.toFloat()
            } else 0f

            IndexedPhoto(
                id = row.id,
                uri = contentUri.toString(),
                hue = if (isMonochrome) null else hue,
                saturation = sat,
                brightness = brightness,
                isMonochrome = isMonochrome,
                dominantColorArgb = swatches[chosenIdx].rgb.toLong(),
                aspectRatio = aspectRatio,
                colorDominance = colorDominance,
            )
        } catch (t: Throwable) {
            // Defensive: BitmapFactory can throw OOM on huge / malformed images.
            Log.e(TAG, "Failed to index $contentUri", t)
            null
        }
    }

    private fun calculateInSampleSize(srcWidth: Int, srcHeight: Int): Int {
        var inSampleSize = 1
        if (srcHeight > PALETTE_TARGET_PX || srcWidth > PALETTE_TARGET_PX) {
            val halfHeight = srcHeight / 2
            val halfWidth = srcWidth / 2
            while (halfHeight / inSampleSize >= PALETTE_TARGET_PX &&
                halfWidth / inSampleSize >= PALETTE_TARGET_PX
            ) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    companion object {
        private const val TAG = "AndroidPhotoIndexer"
        private const val PALETTE_TARGET_PX = 50
        private const val PALETTE_MAX_COLORS = 8
        private const val SAT_THRESHOLD = 0.2f
        private const val VAL_THRESHOLD = 0.15f
        private const val MONO_VAL_THRESHOLD = 0.1f
        private const val BATCH_SIZE = 25
        private const val PROGRESS_EMIT_EVERY = 10
        private const val MAX_WORKERS = 16
    }
}
