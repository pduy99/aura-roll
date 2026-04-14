package com.helios.auraroll.onboarding.impl.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import com.helios.auraroll.common.preferences.AppPreferences
import com.helios.auraroll.data.repository.IndexedPhotoRepository
import com.helios.auraroll.onboarding.impl.domain.usecase.ObserveIndexingStateUseCase
import com.helios.auraroll.onboarding.impl.domain.usecase.StartIndexingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val appPreferences: AppPreferences,
    private val indexedPhotoRepository: IndexedPhotoRepository,
    private val startIndexingUseCase: StartIndexingUseCase,
    private val observeIndexingStateUseCase: ObserveIndexingStateUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    private val _effect = Channel<OnboardingEffect>()
    val effect = _effect.receiveAsFlow()

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
                        sendEffect(OnboardingEffect.NavigateToIndexing)
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
        
        // Observe repository count
        viewModelScope.launch {
            indexedPhotoRepository.observeProcessedCount().collect { count ->
                _uiState.update { it.copy(indexingProcessedCount = count) }
            }
        }

        // Observe repository sample colors
        viewModelScope.launch {
            indexedPhotoRepository.observeSampleColors().collect { colors ->
                _uiState.update { it.copy(indexingPaletteColors = colors) }
            }
        }
    }

    fun dispatch(intent: OnboardingIntent) {
        when (intent) {
            OnboardingIntent.GrantPermission -> {
                sendEffect(OnboardingEffect.LaunchPermissionRequest)
            }
            OnboardingIntent.PermissionGranted -> {
                // The use case internally wraps WorkManager.enqueueUniqueWork
                startIndexingUseCase()
                // The `init` block's observer of getWorkInfosForUniqueWorkFlow will 
                // automatically catch this new work, update the state, navigate to the 
                // indexing page, and start observing the database.
            }
            OnboardingIntent.PermissionDenied -> {
                // In a real app, maybe show a rationale dialog or snackbar.
                // For now, we do nothing or reset loading state.
            }
            OnboardingIntent.LearnHowItWorks -> {
                // Future expansion: show bottom sheet or web link
            }
            OnboardingIntent.PermissionRevokedWarning -> {
                _uiState.update { it.copy(permissionPreviouslyRevoked = true) }
            }
            OnboardingIntent.NextAfterIndexing -> {
                _uiState.update { it.copy(page = OnboardingPage.COMPLETE) }
                sendEffect(OnboardingEffect.NavigateToComplete)
            }
            OnboardingIntent.ExploreGallery -> {
                viewModelScope.launch {
                    appPreferences.setOnboardingCompleted(true)
                    sendEffect(OnboardingEffect.NavigateToHome)
                }
            }
        }
    }

    private fun sendEffect(effect: OnboardingEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}
