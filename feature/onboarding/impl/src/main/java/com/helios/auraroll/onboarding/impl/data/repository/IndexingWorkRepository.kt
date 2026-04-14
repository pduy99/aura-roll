package com.helios.auraroll.onboarding.impl.data.repository

import androidx.work.WorkInfo
import kotlinx.coroutines.flow.Flow

data class IndexingWorkState(
    val state: WorkInfo.State,
    val progress: Float
)

interface IndexingWorkRepository {
    fun startIndexing()
    fun observeIndexingWork(): Flow<IndexingWorkState?>
}