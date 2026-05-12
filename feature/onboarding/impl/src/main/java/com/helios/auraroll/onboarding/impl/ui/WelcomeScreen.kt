package com.helios.auraroll.onboarding.impl.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.helios.auraroll.core.designsystem.components.AuraButtonVariant
import com.helios.auraroll.core.designsystem.components.AuraPrimaryButton
import com.helios.auraroll.core.designsystem.theme.AuraRollTheme
import com.helios.auraroll.core.designsystem.theme.OutlineVariant
import com.helios.auraroll.onboarding.impl.ui.components.HeroIllustration

@Composable
fun WelcomeScreen(
    uiState: OnboardingUiState,
    onGrantPermissionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
            .padding(horizontal = 24.dp)
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeroIllustration(
            modifier = Modifier
                .fillMaxWidth()
        )

        val headline = buildAnnotatedString {
            withStyle(SpanStyle(color = MaterialTheme.colorScheme.onBackground)) {
                append("Memories in a ")
            }
            withStyle(SpanStyle(color = MaterialTheme.colorScheme.secondary)) {
                append("new light.")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = headline,
            style = MaterialTheme.typography.displaySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        PrivacyPill()

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

        AuraPrimaryButton(
            text = "Grant Permission",
            onClick = onGrantPermissionClick,
            variant = AuraButtonVariant.White
        )
    }
}

/** Combined pill badge: shield icon + "PRIVACY FIRST" (purple) • "ON-DEVICE ONLY" (muted). */
@Composable
private fun PrivacyPill(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f))
            .border(1.dp, OutlineVariant.copy(alpha = 0.25f), CircleShape)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Shield,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "PRIVACY FIRST",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.secondary,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.width(8.dp))
        // Dot separator
        Text(
            text = "•",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "ON-DEVICE ONLY",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 1.sp
        )
    }
}

@Preview
@Composable
private fun WelcomeScreenPreview() {
    AuraRollTheme {
        WelcomeScreen(
            uiState = OnboardingUiState(
                permissionPreviouslyRevoked = true,
            ),
            onGrantPermissionClick = {}
        )
    }
}
