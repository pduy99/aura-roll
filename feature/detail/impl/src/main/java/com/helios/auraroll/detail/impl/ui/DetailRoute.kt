package com.helios.auraroll.detail.impl.ui

import android.content.Intent
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.helios.auraroll.common.ui.LaunchedEffectHandler
import kotlinx.coroutines.flow.Flow

@Composable
fun DetailRoute(
    viewModel: DetailViewModel,
    photoId: Long,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(photoId) {
        viewModel.load(photoId)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DetailEffectHandler(
        effectFlow = viewModel.effect,
        onAction = viewModel::onAction,
        navigateBack = navigateBack,
    )

    DetailScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
        modifier = modifier,
    )
}

@Composable
private fun DetailEffectHandler(
    effectFlow: Flow<DetailEffect?>,
    onAction: (DetailAction) -> Unit,
    navigateBack: () -> Unit,
) {
    val activity = LocalActivity.current
    LaunchedEffectHandler(
        effectFlow = effectFlow,
        onConsumeEffect = { onAction(DetailAction.ConsumeEffect) },
        onEffect = { effect ->
            when (effect) {
                is DetailEffect.SharePalette -> {
                    val host = activity ?: return@LaunchedEffectHandler
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, effect.text)
                    }
                    host.startActivity(Intent.createChooser(intent, effect.title))
                }
                DetailEffect.NavigateBack -> navigateBack()
            }
        }
    )
}
