package com.helios.auraroll.core.designsystem.theme

import androidx.compose.ui.graphics.Color

// ── Neutral Core ──────────────────────────────────────────────────────────────
// The canvas: deep blacks that let photography be the primary "pigment".

/** General app background — the True Black canvas. */
val Background = Color(0xFF0E0E0E)

/** Recessed depth areas (darkest possible surface). */
val SurfaceContainerLowest = Color(0xFF000000)

/** Main gallery backgrounds. */
val SurfaceContainerLow = Color(0xFF131313)

/** Standard cards and nav bars. */
val SurfaceContainer = Color(0xFF1A1919)

/** Cards that sit one step above the standard surface. */
val SurfaceContainerHigh = Color(0xFF1F1F1F)

/** Elevated overlays and floating menus. */
val SurfaceContainerHighest = Color(0xFF262626)

/** Dimmed surface for depth contrast behind floating elements. */
val SurfaceDim = Color(0xFF0A0A0A)

/** Brightened surface for inverse-depth moments. */
val SurfaceBright = Color(0xFF2E2E2E)

/**
 * Glass surface base — applied at 60 % opacity with a 20 dp backdrop blur
 * to achieve the glassmorphism effect on nav bars and the Spectrum Slider.
 */
val SurfaceVariant = Color(0xFF1A1919)

// ── Primary ───────────────────────────────────────────────────────────────────
// Reserved for high-priority typography and essential icons.

val Primary = Color(0xFFF9F9F9)
val OnPrimary = Color(0xFF0E0E0E)
val PrimaryContainer = Color(0xFF1A1919)
val OnPrimaryContainer = Color(0xFFF9F9F9)
val InversePrimary = Color(0xFF1A1A1A)

// ── Secondary — Vibrant Purple Accent ─────────────────────────────────────────
// Used sparingly: interactive states, Spectrum Slider, moments of delight.

val Secondary = Color(0xFFAC89FF)
val OnSecondary = Color(0xFF0E0E0E)
val SecondaryContainer = Color(0xFF2D1F50)
val OnSecondaryContainer = Color(0xFFD4C0FF)

/** Fixed variant — colour-safe reference unaffected by theme inversion. */
val SecondaryFixed = Color(0xFFAC89FF)

/** Gradient end-stop for primary CTAs; also used for subtle interactive states. */
val SecondaryFixedDim = Color(0xFF8A6BDE)

// ── Tertiary — Vibrant Cyan Accent ────────────────────────────────────────────

val Tertiary = Color(0xFF81ECFF)
val OnTertiary = Color(0xFF0E0E0E)
val TertiaryContainer = Color(0xFF003D47)
val OnTertiaryContainer = Color(0xFFB3F5FF)
val TertiaryFixed = Color(0xFF81ECFF)
val TertiaryFixedDim = Color(0xFF5DC0D0)

// ── On-surfaces ───────────────────────────────────────────────────────────────

val OnBackground = Color(0xFFF9F9F9)
val OnSurface = Color(0xFFF9F9F9)

/** Secondary text / metadata labels. */
val OnSurfaceVariant = Color(0xFFAAAAAA)

/** Tint colour bled through glass surfaces (maps to Secondary). */
val SurfaceTint = Secondary

val InverseSurface = Color(0xFFF9F9F9)
val InverseOnSurface = Color(0xFF0E0E0E)

// ── Boundaries ────────────────────────────────────────────────────────────────
// Prefer background shifts over lines; only use Outline tokens when required.

val Outline = Color(0xFF3D3D3D)

/** "Ghost Border" — should be felt, not seen; apply at ≤ 15 % opacity. */
val OutlineVariant = Color(0xFF262626)

val Scrim = Color(0xFF000000)

// ── Error ─────────────────────────────────────────────────────────────────────

val Error = Color(0xFFCF6679)
val OnError = Color(0xFF1A0009)
val ErrorContainer = Color(0xFF3D0014)
val OnErrorContainer = Color(0xFFFFB3BE)
