package com.helios.auraroll.onboarding.impl.ui

import androidx.compose.runtime.Stable

enum class OnboardingPage {
    WELCOME,
    INDEXING,
    COMPLETE
}

@Stable
data class OnboardingUiState(
    val page: OnboardingPage = OnboardingPage.WELCOME,
    val permissionPreviouslyRevoked: Boolean = false,
    val isLoading: Boolean = false,
    val indexingProgress: Float = 0f,
    val indexingProcessedCount: Int = 0,
    val indexingPaletteColors: List<Long> = emptyList()
)

sealed interface OnboardingIntent {
    data object GrantPermission : OnboardingIntent
    data object PermissionGranted : OnboardingIntent
    data object PermissionDenied : OnboardingIntent
    data object LearnHowItWorks : OnboardingIntent
    data object PermissionRevokedWarning : OnboardingIntent
    data object NextAfterIndexing : OnboardingIntent
    data object ExploreGallery : OnboardingIntent
}

sealed interface OnboardingEffect {
    data object LaunchPermissionRequest : OnboardingEffect
    data object NavigateToIndexing : OnboardingEffect
    data object NavigateToComplete : OnboardingEffect
    data object NavigateToHome : OnboardingEffect
}
