package com.helios.auraroll.detail.impl.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.helios.auraroll.detail.impl.ui.components.ColorIntelligenceSection
import com.helios.auraroll.detail.impl.ui.components.DetailTopBar
import com.helios.auraroll.detail.impl.ui.components.ExportPaletteButton
import com.helios.auraroll.detail.impl.ui.components.HeroPhotoCard
import com.helios.auraroll.detail.impl.ui.components.ProMetadataCard
import com.helios.auraroll.detail.impl.ui.components.TemperatureBalanceBar

@Composable
fun DetailScreen(
    uiState: DetailUiState,
    onAction: (DetailAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when {
            uiState.isLoading && uiState.detail == null -> LoadingState()
            uiState.detail != null -> LoadedContent(
                state = uiState,
                onAction = onAction,
            )
            else -> ErrorState(message = uiState.errorMessage)
        }
    }
}

@Composable
private fun LoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary)
    }
}

@Composable
private fun ErrorState(message: String?) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = message ?: "Something went wrong.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(24.dp),
        )
    }
}

@Composable
private fun LoadedContent(
    state: DetailUiState,
    onAction: (DetailAction) -> Unit,
) {
    val detail = state.detail ?: return

    Column(modifier = Modifier.fillMaxSize()) {
        DetailTopBar(
            title = "Aura Roll",
            onBack = { onAction(DetailAction.BackClicked) },
            onShare = { onAction(DetailAction.ShareClicked) },
            onMore = { onAction(DetailAction.MoreClicked) },
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp)
        ) {
            HeroPhotoCard(
                uri = detail.uri,
                aspectRatio = detail.aspectRatio,
                fileName = detail.fileName,
                location = detail.location,
            )

            Spacer(Modifier.height(28.dp))

            ColorIntelligenceSection(
                palette = detail.palette,
                description = detail.description,
                dominantHueLabel = detail.dominantHueLabel,
                accentHueLabel = detail.accentHueLabel,
            )

            Spacer(Modifier.height(20.dp))

            TemperatureBalanceBar(temperature = detail.temperatureBalance)

            Spacer(Modifier.height(24.dp))

            ProMetadataCard(
                exif = detail.exif,
                histogram = detail.histogram,
            )

            Spacer(Modifier.height(24.dp))

            ExportPaletteButton(
                onClick = { onAction(DetailAction.ExportPaletteClicked) },
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            )
        }
    }
}


