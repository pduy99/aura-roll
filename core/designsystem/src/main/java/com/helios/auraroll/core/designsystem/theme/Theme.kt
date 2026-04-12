package com.helios.auraroll.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

/**
 * AuraRoll dark color scheme — "The Living Curator" palette.
 *
 * Deep neutral bases ensure the user's photography provides the primary
 * "pigment" of the experience. Vibrant accents (Secondary / Tertiary) are
 * used sparingly for interactive states and moments of delight.
 */
private val AuraRollColorScheme = darkColorScheme(
    // ── Primary ────────────────────────────��──────────────────────────────
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    inversePrimary = InversePrimary,

    // ── Secondary (Vibrant Purple) ────────────────────────────────────────
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,
    secondaryFixed = SecondaryFixed,
    secondaryFixedDim = SecondaryFixedDim,

    // ── Tertiary (Vibrant Cyan) ───────────────────────────────────────────
    tertiary = Tertiary,
    onTertiary = OnTertiary,
    tertiaryContainer = TertiaryContainer,
    onTertiaryContainer = OnTertiaryContainer,
    tertiaryFixed = TertiaryFixed,
    tertiaryFixedDim = TertiaryFixedDim,

    // ── Backgrounds & Surfaces ────────────────────────────────────────────
    background = Background,
    onBackground = OnBackground,
    surface = SurfaceContainerLow,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant,
    surfaceTint = SurfaceTint,
    surfaceDim = SurfaceDim,
    surfaceBright = SurfaceBright,
    surfaceContainerLowest = SurfaceContainerLowest,
    surfaceContainerLow = SurfaceContainerLow,
    surfaceContainer = SurfaceContainer,
    surfaceContainerHigh = SurfaceContainerHigh,
    surfaceContainerHighest = SurfaceContainerHighest,
    inverseSurface = InverseSurface,
    inverseOnSurface = InverseOnSurface,

    // ── Boundaries ("No-Line" rule) ───────────────────────────────────────
    // Prefer background shifts over visible lines.
    outline = Outline,
    outlineVariant = OutlineVariant,   // use at ≤ 15 % opacity ("Ghost Border")
    scrim = Scrim,

    // ── Error ─────────────────────────────────────────────────────────────
    error = Error,
    onError = OnError,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer,
)

@Composable
fun AuraRollTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = AuraRollColorScheme,
        typography = AuraRollTypography,
        content = content,
    )
}
