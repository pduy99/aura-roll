package com.helios.auraroll.onboarding.impl.data.repository

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.helios.auraroll.onboarding.impl.work.IndexingWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WorkManagerIndexingRepository @Inject constructor(
    @ApplicationContext private val context: Context
) : IndexingWorkRepository {

    override fun startIndexing() {
        val workManager = WorkManager.getInstance(context)
        val request = OneTimeWorkRequestBuilder<IndexingWorker>().build()

        workManager.enqueueUniqueWork(
            IndexingWorker.WORK_NAME,
            ExistingWorkPolicy.KEEP,
            request
        )
    }

    override fun observeIndexingWork(): Flow<IndexingWorkState?> {
        val workManager = WorkManager.getInstance(context)
        return workManager.getWorkInfosForUniqueWorkFlow(IndexingWorker.WORK_NAME)
            .map { workInfos ->
                val workInfo = workInfos.firstOrNull() ?: return@map null
                val progress = workInfo.progress.getFloat(
                    IndexingWorker.KEY_PROGRESS, 
                    if (workInfo.state == WorkInfo.State.SUCCEEDED) 1f else 0f
                )
                IndexingWorkState(state = workInfo.state, progress = progress)
            }
    }
}