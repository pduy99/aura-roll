package com.helios.auraroll.data.repository

import kotlinx.coroutines.flow.Flow

interface IndexedPhotoRepository {
    fun observeProcessedCount(): Flow<Int>
    fun observeSampleColors(): Flow<List<Long>>
}