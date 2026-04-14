package com.helios.auraroll.onboarding.impl.domain.usecase

import com.helios.auraroll.onboarding.impl.data.repository.IndexingWorkRepository
import com.helios.auraroll.onboarding.impl.data.repository.IndexingWorkState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveIndexingStateUseCase @Inject constructor(
    private val repository: IndexingWorkRepository
) {
    operator fun invoke(): Flow<IndexingWorkState?> {
        return repository.observeIndexingWork()
    }
}