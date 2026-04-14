package com.helios.auraroll.onboarding.impl.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.helios.auraroll.core.designsystem.components.AuraButtonVariant
import com.helios.auraroll.core.designsystem.components.AuraPrimaryButton
import com.helios.auraroll.core.designsystem.theme.AuraRollTheme
import com.helios.auraroll.core.designsystem.theme.SecondaryFixed
import com.helios.auraroll.core.designsystem.theme.TertiaryFixed
import com.helios.auraroll.onboarding.impl.R
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CompleteScreen(
    uiState: OnboardingUiState,
    onExploreGalleryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formattedCount =
        NumberFormat.getNumberInstance(Locale.US).format(uiState.indexingProcessedCount)

    // Gentle pulsing glow animation for the success icon
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowScale"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f),
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
        Spacer(modifier = Modifier.weight(0.15f))

        // ── Aurora success icon ───────────────────────────────────────────────
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(140.dp)
        ) {
            // Soft glow ring behind the icon
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .scale(glowScale)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.18f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Icon container
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
                                MaterialTheme.colorScheme.surfaceContainer
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(SecondaryFixed.copy(alpha = 0.4f), TertiaryFixed.copy(alpha = 0.2f))
                        ),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.img_hero_swirl),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(56.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // ── Headline ─────────────────────────────────────────────────────────
        Text(
            text = "Your aura is ready.",
            style = MaterialTheme.typography.displaySmall.copy(
                brush = Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.onBackground,
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.9f)
                    )
                )
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ── Body text ────────────────────────────��───────────────────────────
        Text(
            text = "Your gallery has been indexed and is ready to explore. Every memory now has a colour.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        // ── Summary card ─────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f),
                            MaterialTheme.colorScheme.surfaceContainer
                        )
                    )
                )
                .padding(28.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {

                // Card label
                Text(
                    text = "INDEXING COMPLETE",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Stats row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    // Memories indexed stat
                    Column {
                        Text(
                            text = "MEMORIES INDEXED",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = formattedCount,
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Icon(
                                imageVector = Icons.Filled.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Palette swatches
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "COLOUR PALETTE",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy((-6).dp)) {
                            uiState.indexingPaletteColors.take(5).forEach { colorLong ->
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(Color(colorLong))
                                        .border(
                                            1.dp,
                                            MaterialTheme.colorScheme.surfaceContainer,
                                            CircleShape
                                        )
                                )
                            }
                            if (uiState.indexingPaletteColors.size < 5) {
                                repeat(5 - uiState.indexingPaletteColors.size) {
                                    Box(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
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

                Spacer(modifier = Modifier.height(24.dp))

                // Gradient divider
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    SecondaryFixed.copy(alpha = 0.5f),
                                    TertiaryFixed.copy(alpha = 0.2f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // On-device note
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "Processed entirely on-device. Your photos never left.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // ── Primary CTA ───────────────────────────────────────────────────────
        AuraPrimaryButton(
            text = "Explore Gallery",
            onClick = onExploreGalleryClick,
            variant = AuraButtonVariant.Purple
        )
    }
}

@Preview
@Composable
private fun CompleteScreenPreview() {
    AuraRollTheme {
        CompleteScreen(
            uiState = OnboardingUiState(
                page = OnboardingPage.COMPLETE,
                indexingProcessedCount = 3_421,
                indexingPaletteColors = listOf(
                    0xFFF5D6C6,
                    0xFF6B8E78,
                    0xFFA9C2F0,
                    0xFF4A3F72,
                    0xFFF9F9F9
                )
            ),
            onExploreGalleryClick = {}
        )
    }
}
