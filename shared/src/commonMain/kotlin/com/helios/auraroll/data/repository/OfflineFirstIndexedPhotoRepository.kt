package com.helios.auraroll.data.repository

import com.helios.auraroll.database.IndexedPhotoDao
import kotlinx.coroutines.flow.Flow

class OfflineFirstIndexedPhotoRepository(
    private val dao: IndexedPhotoDao
) : IndexedPhotoRepository {

    override fun observeProcessedCount(): Flow<Int> = dao.countAll()

    override fun observeSampleColors(): Flow<List<Long>> = dao.sampleColors()
}