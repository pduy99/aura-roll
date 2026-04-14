package com.helios.auraroll.onboarding.impl.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.helios.auraroll.indexing.PhotoIndexer
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class IndexingWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val photoIndexer: PhotoIndexer
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            photoIndexer.index().collect { state ->
                setProgress(
                    workDataOf(
                        KEY_PROGRESS to state.progress,
                        KEY_COUNT to state.processedCount
                    )
                )
            }
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

    companion object {
        const val KEY_PROGRESS = "progress"
        const val KEY_COUNT = "count"
        const val WORK_NAME = "photo_indexing_work"
    }
}