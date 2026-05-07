package com.helios.auraroll.onboarding.impl.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import com.helios.auraroll.common.preferences.AppPreferences
import com.helios.auraroll.data.repository.IndexedPhotoRepository
import com.helios.auraroll.onboarding.IndexingTipRepository
import com.helios.auraroll.onboarding.impl.domain.usecase.ObserveIndexingStateUseCase
import com.helios.auraroll.onboarding.impl.domain.usecase.StartIndexingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val appPreferences: AppPreferences,
    private val indexedPhotoRepository: IndexedPhotoRepository,
    private val startIndexingUseCase: StartIndexingUseCase,
    private val observeIndexingStateUseCase: ObserveIndexingStateUseCase,
    indexingTipRepository: IndexingTipRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState(indexingTips = indexingTipRepository.tips()))
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    val effect: Flow<OnboardingEffect?> = uiState.map { it.effect }

    private var databaseObserved = false

    init {
        viewModelScope.launch {
            observeIndexingStateUseCase().collect { workState ->
                if (workState != null) {
                    val isIndexingOrDone = workState.state == WorkInfo.State.ENQUEUED ||
                        workState.state == WorkInfo.State.RUNNING ||
                        workState.state == WorkInfo.State.SUCCEEDED

                    if (isIndexingOrDone && _uiState.value.page == OnboardingPage.WELCOME) {
                        _uiState.update { it.copy(page = OnboardingPage.INDEXING) }
                        observeDatabase()
                    }

                    _uiState.update { it.copy(indexingProgress = workState.progress) }
                }
            }
        }
    }

    private fun observeDatabase() {
        if (databaseObserved) return
        databaseObserved = true

        viewModelScope.launch {
            indexedPhotoRepository.observeProcessedCount().collect { count ->
                _uiState.update { it.copy(indexingProcessedCount = count) }
            }
        }

        viewModelScope.launch {
            indexedPhotoRepository.observeSampleColors().collect { colors ->
                _uiState.update { it.copy(indexingPaletteColors = colors) }
            }
        }
    }

    fun onAction(action: OnboardingAction) {
        when (action) {
            OnboardingAction.GrantPermission -> {
                _uiState.update { it.copy(effect = OnboardingEffect.LaunchPermissionRequest) }
            }
            OnboardingAction.PermissionGranted -> {
                // The use case wraps WorkManager.enqueueUniqueWork; the `init` block's
                // observer will pick up the new work, transition to INDEXING, and start
                // observing the database.
                startIndexingUseCase()
            }
            OnboardingAction.PermissionDenied -> {
                // Future: show rationale dialog or snackbar via a new effect.
            }
            OnboardingAction.PermissionRevokedWarning -> {
                _uiState.update { it.copy(permissionPreviouslyRevoked = true) }
            }
            OnboardingAction.ExploreGallery -> {
                viewModelScope.launch {
                    appPreferences.setOnboardingCompleted(true)
                    _uiState.update { it.copy(effect = OnboardingEffect.NavigateToHome) }
                }
            }
            OnboardingAction.ConsumeEffect -> {
                _uiState.update { it.copy(effect = null) }
            }
        }
    }
}
