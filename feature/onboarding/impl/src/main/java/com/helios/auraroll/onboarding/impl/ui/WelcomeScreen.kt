package com.helios.auraroll.onboarding.impl.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.helios.auraroll.core.designsystem.components.AuraBadge
import com.helios.auraroll.core.designsystem.components.AuraButtonVariant
import com.helios.auraroll.core.designsystem.components.AuraGhostButton
import com.helios.auraroll.core.designsystem.components.AuraPrimaryButton
import com.helios.auraroll.core.designsystem.theme.AuraRollTheme
import com.helios.auraroll.onboarding.impl.ui.components.HeroIllustration

@Composable
fun WelcomeScreen(
    uiState: OnboardingUiState,
    onGrantPermissionClick: () -> Unit,
    onLearnHowItWorksClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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
        // Hero Illustration area
        HeroIllustration(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )

        // Headline
        Text(
            text = "See your memories in a new light.",
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

        // Body text
        Text(
            text = "Aura Roll re-imagines your photo library as a spectrum of color, emotion, and texture.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Privacy Badge
        AuraBadge(
            text = "PRIVACY FIRST",
            icon = Icons.Outlined.Shield
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Privacy Subtext
        Text(
            text = "Zero cloud processing. All analysis happens securely on-device.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))

        if (uiState.permissionPreviouslyRevoked) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.ErrorOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
                Text(
                    text = "Photo access was revoked. Please grant permission to continue using Aura Roll.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Buttons
        Column {
            AuraPrimaryButton(
                text = "Grant Permission",
                onClick = onGrantPermissionClick,
                variant = AuraButtonVariant.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            AuraGhostButton(
                text = "Learn how it works",
                onClick = onLearnHowItWorksClick
            )
        }
    }
}

@Preview
@Composable
private fun WelcomeScreenPreview() {
    AuraRollTheme {
        WelcomeScreen(
            uiState = OnboardingUiState(),
            onGrantPermissionClick = {},
            onLearnHowItWorksClick = {}
        )
    }
}