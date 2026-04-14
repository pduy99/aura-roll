package com.helios.auraroll.onboarding.impl.domain.usecase

import com.helios.auraroll.onboarding.impl.data.repository.IndexingWorkRepository
import javax.inject.Inject

class StartIndexingUseCase @Inject constructor(
    private val repository: IndexingWorkRepository
) {
    operator fun invoke() {
        repository.startIndexing()
    }
}