package com.helios.auraroll.onboarding.impl.ui

import androidx.compose.runtime.Stable
import com.helios.auraroll.onboarding.IndexingTip

enum class OnboardingPage {
    WELCOME,
    INDEXING
}

@Stable
data class OnboardingUiState(
    val page: OnboardingPage = OnboardingPage.WELCOME,
    val permissionPreviouslyRevoked: Boolean = false,
    val isLoading: Boolean = false,
    val indexingProgress: Float = 0f,
    val indexingProcessedCount: Int = 0,
    val indexingPaletteColors: List<Long> = emptyList(),
    val indexingTips: List<IndexingTip> = emptyList(),
    val effect: OnboardingEffect? = null,
)

sealed interface OnboardingAction {
    data object GrantPermission : OnboardingAction
    data object PermissionGranted : OnboardingAction
    data object PermissionDenied : OnboardingAction
    data object PermissionRevokedWarning : OnboardingAction
    data object ExploreGallery : OnboardingAction
    data object ConsumeEffect : OnboardingAction
}

sealed interface OnboardingEffect {
    data object LaunchPermissionRequest : OnboardingEffect
    data object NavigateToHome : OnboardingEffect
}
