package com.helios.auraroll.onboarding.impl.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.helios.auraroll.common.ui.LaunchedEffectHandler
import kotlinx.coroutines.flow.Flow

@Composable
fun OnboardingRoute(
    viewModel: OnboardingViewModel,
    navigateToHome: () -> Unit,
    modifier: Modifier = Modifier,
    permissionRevoked: Boolean = false
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            // If any of the requested media permissions are granted, treat as success.
            // This covers full access (READ_MEDIA_IMAGES) or partial access
            // (READ_MEDIA_VISUAL_USER_SELECTED).
            val isGranted = permissions.values.any { it }
            if (isGranted) {
                viewModel.onAction(OnboardingAction.PermissionGranted)
            } else {
                viewModel.onAction(OnboardingAction.PermissionDenied)
            }
        }
    )

    LaunchedEffect(permissionRevoked) {
        if (permissionRevoked) {
            viewModel.onAction(OnboardingAction.PermissionRevokedWarning)
        }
    }

    OnboardingEffectHandler(
        effectFlow = viewModel.effect,
        onAction = viewModel::onAction,
        onLaunchPermissionRequest = {
            val permissions = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
                )
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES
                )
                else -> arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            permissionLauncher.launch(permissions)
        },
        onNavigateToHome = navigateToHome,
    )

    when (uiState.page) {
        OnboardingPage.WELCOME -> {
            WelcomeScreen(
                uiState = uiState,
                onGrantPermissionClick = { viewModel.onAction(OnboardingAction.GrantPermission) },
                modifier = modifier
            )
        }
        OnboardingPage.INDEXING -> {
            IndexingScreen(
                uiState = uiState,
                onEnterSpectrumClick = { viewModel.onAction(OnboardingAction.ExploreGallery) },
                modifier = modifier
            )
        }
    }
}

@Composable
private fun OnboardingEffectHandler(
    effectFlow: Flow<OnboardingEffect?>,
    onAction: (OnboardingAction) -> Unit,
    onLaunchPermissionRequest: () -> Unit,
    onNavigateToHome: () -> Unit,
) {
    LaunchedEffectHandler(
        effectFlow = effectFlow,
        onConsumeEffect = { onAction(OnboardingAction.ConsumeEffect) },
        onEffect = { effect ->
            when (effect) {
                OnboardingEffect.LaunchPermissionRequest -> onLaunchPermissionRequest()
                OnboardingEffect.NavigateToHome -> onNavigateToHome()
            }
        }
    )
}
