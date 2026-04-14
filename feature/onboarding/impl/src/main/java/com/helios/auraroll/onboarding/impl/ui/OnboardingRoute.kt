package com.helios.auraroll.onboarding.impl.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun OnboardingRoute(
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel,
    permissionRevoked: Boolean = false
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            // If any of the requested media permissions are granted, we consider it a success
            // This covers full access (READ_MEDIA_IMAGES) or partial access (READ_MEDIA_VISUAL_USER_SELECTED)
            val isGranted = permissions.values.any { it }
            if (isGranted) {
                viewModel.dispatch(OnboardingIntent.PermissionGranted)
            } else {
                viewModel.dispatch(OnboardingIntent.PermissionDenied)
            }
        }
    )

    LaunchedEffect(permissionRevoked) {
        if (permissionRevoked) {
            viewModel.dispatch(OnboardingIntent.PermissionRevokedWarning)
        }
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is OnboardingEffect.LaunchPermissionRequest -> {
                    // Launch permission request depending on Android version
                    val permissions = when {
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                            arrayOf(
                                Manifest.permission.READ_MEDIA_IMAGES,
                                Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
                            )
                        }
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                            arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
                        }
                        else -> {
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }
                    }
                    permissionLauncher.launch(permissions)
                }
                is OnboardingEffect.NavigateToIndexing -> {
                    // Handled within the route rendering the next screen
                }
                is OnboardingEffect.NavigateToComplete -> {
                    // Handled by rendering CompleteScreen below based on page state
                }
                is OnboardingEffect.NavigateToHome -> {
                    // TODO: Navigate to the main gallery destination once it is created
                    // navigator.popAndGoTo(Home)
                }
            }
        }
    }

    when (uiState.page) {
        OnboardingPage.WELCOME -> {
            WelcomeScreen(
                uiState = uiState,
                onGrantPermissionClick = { viewModel.dispatch(OnboardingIntent.GrantPermission) },
                onLearnHowItWorksClick = { viewModel.dispatch(OnboardingIntent.LearnHowItWorks) },
                modifier = modifier
            )
        }
        OnboardingPage.INDEXING -> {
            IndexingScreen(
                uiState = uiState,
                onNextClick = { viewModel.dispatch(OnboardingIntent.NextAfterIndexing) },
                modifier = modifier
            )
        }
        OnboardingPage.COMPLETE -> {
            CompleteScreen(
                uiState = uiState,
                onExploreGalleryClick = { viewModel.dispatch(OnboardingIntent.ExploreGallery) },
                modifier = modifier
            )
        }
    }
}
