package com.helios.auraroll.onboarding.impl.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.helios.auraroll.core.designsystem.components.AuraButtonVariant
import com.helios.auraroll.core.designsystem.components.AuraPrimaryButton
import com.helios.auraroll.core.designsystem.theme.AuraRollTheme
import com.helios.auraroll.core.designsystem.theme.SecondaryFixed
import com.helios.auraroll.core.designsystem.theme.SurfaceContainerLowest
import com.helios.auraroll.core.designsystem.theme.TertiaryFixed
import com.helios.auraroll.onboarding.IndexingTip
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.util.Locale

@Composable
fun IndexingScreen(
    uiState: OnboardingUiState,
    onEnterSpectrumClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progressAnim by animateFloatAsState(
        targetValue = uiState.indexingProgress,
        animationSpec = tween(durationMillis = 500),
        label = "progress"
    )
    val percent = (progressAnim * 100).toInt().coerceIn(0, 100)

    val formattedCount =
        NumberFormat.getNumberInstance(Locale.US).format(uiState.indexingProcessedCount)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .systemBarsPadding()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(0.1f))

        // Headline
        Text(
            text = "Organizing your world.",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Body text
        Text(
            text = "Experience a silent, sophisticated gallery tailored to your unique visual journey.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        // System Status Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f),
                            MaterialTheme.colorScheme.surfaceContainer
                        )
                    )
                )
                .padding(32.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Badge
                Text(
                    text = "SYSTEM STATUS",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Progress Label Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "Indexing Aura...",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "$percent%",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Progress Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape)
                        .background(SurfaceContainerLowest)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progressAnim)
                            .height(6.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(SecondaryFixed, TertiaryFixed)
                                )
                            )
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Bottom stats row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Processed Memories
                    Column {
                        Text(
                            text = "PROCESSED MEMORIES",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = formattedCount,
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Filled.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Detected Palettes
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "DETECTED PALETTES",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy((-6).dp)) {
                            uiState.indexingPaletteColors.take(5).forEach { colorLong ->
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(Color(colorLong))
                                        .border(
                                            1.dp,
                                            MaterialTheme.colorScheme.surfaceContainer,
                                            CircleShape
                                        )
                                )
                            }
                            // Empty slot for design consistency if less than 5
                            if (uiState.indexingPaletteColors.size < 5) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(Color.Transparent)
                                        .border(
                                            1.dp,
                                            MaterialTheme.colorScheme.outlineVariant,
                                            CircleShape
                                        )
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Rotating tips carousel
        RotatingTip(tips = uiState.indexingTips)

        Spacer(modifier = Modifier.height(24.dp))

        // Enter the spectrum button, only shown once indexing is complete
        AnimatedVisibility(visible = uiState.indexingProgress >= 1f) {
            AuraPrimaryButton(
                text = "ENTER THE SPECTRUM",
                onClick = onEnterSpectrumClick,
                variant = AuraButtonVariant.White
            )
        }
    }
}

@Composable
private fun RotatingTip(
    tips: List<IndexingTip>,
    modifier: Modifier = Modifier,
    intervalMillis: Long = 5_000L
) {
    if (tips.isEmpty()) return

    var index by remember { mutableIntStateOf(0) }

    LaunchedEffect(tips) {
        index = 0
        while (tips.size > 1) {
            delay(intervalMillis)
            index = (index + 1) % tips.size
        }
    }

    AnimatedContent(
        targetState = index.coerceIn(0, tips.lastIndex),
        transitionSpec = {
            fadeIn(animationSpec = tween(durationMillis = 400)) togetherWith
                fadeOut(animationSpec = tween(durationMillis = 400))
        },
        label = "rotatingTip",
        modifier = modifier.fillMaxWidth()
    ) { current ->
        Text(
            text = "Tip: ${tips[current].text}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
    }
}

@Preview
@Composable
private fun IndexingScreenPreview() {
    AuraRollTheme {
        IndexingScreen(
            uiState = OnboardingUiState(
                indexingProgress = 0.74f,
                indexingProcessedCount = 2304,
                indexingPaletteColors = listOf(0xFFF5D6C6, 0xFF6B8E78, 0xFFA9C2F0, 0xFFF9F9F9),
                indexingTips = listOf(
                    IndexingTip("Aura Roll indexes everything offline first."),
                    IndexingTip("You can safely close the app; indexing continues in the background.")
                )
            ),
            onEnterSpectrumClick = {}
        )
    }
}