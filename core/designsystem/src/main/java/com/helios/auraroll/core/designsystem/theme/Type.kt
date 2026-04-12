package com.helios.auraroll.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.helios.auraroll.core.designsystem.R

// ── Google Fonts provider ─────────────────────────────────────────────────────
// Certificate values are bundled in res/values/font_certs.xml of this module.

private val googleFontProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs,
)

// ── Font families ─────────────────────────────────────────────────────────────

/**
 * Manrope — geometric, "tech-editorial" precision.
 * Used for Display & Headline styles (large, light-weight, magazine aesthetic).
 */
val ManropeFontFamily = FontFamily(
    Font(
        googleFont = GoogleFont("Manrope"),
        fontProvider = googleFontProvider,
        weight = FontWeight.Light,
    ),
    Font(
        googleFont = GoogleFont("Manrope"),
        fontProvider = googleFontProvider,
        weight = FontWeight.Normal,
    ),
    Font(
        googleFont = GoogleFont("Manrope"),
        fontProvider = googleFontProvider,
        weight = FontWeight.Medium,
    ),
    Font(
        googleFont = GoogleFont("Manrope"),
        fontProvider = googleFontProvider,
        weight = FontWeight.SemiBold,
    ),
    Font(
        googleFont = GoogleFont("Manrope"),
        fontProvider = googleFontProvider,
        weight = FontWeight.Bold,
    ),
)

/**
 * Inter — maximum legibility at small scales.
 * Used for Body & Label styles (metadata, technical photo data).
 */
val InterFontFamily = FontFamily(
    Font(
        googleFont = GoogleFont("Inter"),
        fontProvider = googleFontProvider,
        weight = FontWeight.Light,
    ),
    Font(
        googleFont = GoogleFont("Inter"),
        fontProvider = googleFontProvider,
        weight = FontWeight.Normal,
    ),
    Font(
        googleFont = GoogleFont("Inter"),
        fontProvider = googleFontProvider,
        weight = FontWeight.Medium,
    ),
    Font(
        googleFont = GoogleFont("Inter"),
        fontProvider = googleFontProvider,
        weight = FontWeight.SemiBold,
    ),
)

// ── Typography scale ──────────────────────────────────────────────────────────
// Hierarchy principle: high contrast in *scale*, not weight.
// Large, light headlines + small, crisp body text = premium magazine feel.

/**
 * AuraRoll editorial typography scale.
 *
 * Display / Headlines → Manrope (geometric, editorial)
 * Body / Labels       → Inter  (crisp, legible at small sizes)
 */
val AuraRollTypography = Typography(
    // ── Display (Manrope) ────��─────────────────────────────────────────────
    // display-lg ≈ 3.5 rem → used for empty states and "Memory" headers.
    displayLarge = TextStyle(
        fontFamily = ManropeFontFamily,
        fontWeight = FontWeight.Light,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp,
    ),
    displayMedium = TextStyle(
        fontFamily = ManropeFontFamily,
        fontWeight = FontWeight.Light,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp,
    ),
    displaySmall = TextStyle(
        fontFamily = ManropeFontFamily,
        fontWeight = FontWeight.Light,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp,
    ),

    // ── Headlines (Manrope) ────────────────────────────────────────────────
    headlineLarge = TextStyle(
        fontFamily = ManropeFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = ManropeFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp,
    ),
    // headline-sm ≈ 1.5 rem → album titles.
    headlineSmall = TextStyle(
        fontFamily = ManropeFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp,
    ),

    // ── Titles (Manrope large / Inter small) ──────────────────────────────
    titleLarge = TextStyle(
        fontFamily = ManropeFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp,
    ),
    titleSmall = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    ),

    // ── Body (Inter) ──────────────────────────────────────────────────────
    bodyLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
    ),
    // body-md ≈ 0.875 rem → metadata text.
    bodyMedium = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp,
    ),

    // ── Labels (Inter) ────────────────────────────────────────────────────
    labelLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    ),
    // label-sm ≈ 0.6875 rem → technical photo data (ISO, Shutter speed).
    labelSmall = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    ),
)
