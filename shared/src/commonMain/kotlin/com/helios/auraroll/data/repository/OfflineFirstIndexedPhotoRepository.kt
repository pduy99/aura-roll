package com.helios.auraroll.data.repository

import com.helios.auraroll.data.model.Photo
import com.helios.auraroll.database.IndexedPhoto
import com.helios.auraroll.database.IndexedPhotoDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class OfflineFirstIndexedPhotoRepository(
    private val dao: IndexedPhotoDao
) : IndexedPhotoRepository {

    override fun observeProcessedCount(): Flow<Int> = dao.countAll()

    override fun observeSampleColors(): Flow<List<Long>> = dao.sampleColors()

    override fun observePhotosByHue(
        centerHue: Float,
        toleranceDeg: Float,
        minDominance: Float
    ): Flow<List<Photo>> {
        val min = ((centerHue - toleranceDeg) + 360f) % 360f
        val max = (centerHue + toleranceDeg) % 360f
        val entityFlow: Flow<List<IndexedPhoto>> = if (min <= max) {
            dao.observePhotosByHueRange(min, max, minDominance)
        } else {
            // Wrap-around case (e.g. red at 0°/360°): query two arcs and merge
            combine(
                dao.observePhotosByHueRange(min, 360f, minDominance),
                dao.observePhotosByHueRange(0f, max, minDominance)
            ) { upper, lower ->
                (upper + lower).sortedWith(
                    compareByDescending<IndexedPhoto> { it.colorDominance }.thenByDescending { it.id }
                )
            }
        }
        return entityFlow.map { list -> list.map(IndexedPhoto::toPhoto) }
    }

    override fun observePhotoCountByHue(
        centerHue: Float,
        toleranceDeg: Float,
        minDominance: Float
    ): Flow<Int> {
        val min = ((centerHue - toleranceDeg) + 360f) % 360f
        val max = (centerHue + toleranceDeg) % 360f
        return if (min <= max) {
            dao.countPhotosByHueRange(min, max, minDominance)
        } else {
            combine(
                dao.countPhotosByHueRange(min, 360f, minDominance),
                dao.countPhotosByHueRange(0f, max, minDominance)
            ) { upper, lower -> upper + lower }
        }
    }

    override fun observeMonochromePhotos(): Flow<List<Photo>> =
        dao.observeMonochromePhotos().map { list -> list.map(IndexedPhoto::toPhoto) }

    override fun observeMonochromePhotoCount(): Flow<Int> =
        dao.countMonochromePhotos()
}
