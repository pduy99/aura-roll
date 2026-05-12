package com.helios.auraroll.detail.impl.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import kotlin.math.max
import kotlin.math.min
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.helios.auraroll.detail.PaletteSwatch

@Composable
fun ColorIntelligenceSection(
    palette: List<PaletteSwatch>,
    description: String,
    dominantHueLabel: String,
    accentHueLabel: String?,
    modifier: Modifier = Modifier,
) {
    val surface = MaterialTheme.colorScheme.background
    Column(modifier = modifier.fillMaxWidth()) {
        SectionHeader()
        Spacer(Modifier.height(16.dp))
        PaletteRow(palette = palette)
        Spacer(Modifier.height(16.dp))
        Text(
            text = highlightHueNames(description, palette, dominantHueLabel, accentHueLabel, surface),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun SectionHeader() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Outlined.Palette,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(22.dp),
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = "Color Intelligence",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@Composable
private fun PaletteRow(palette: List<PaletteSwatch>) {
    if (palette.isEmpty()) return
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        palette.forEach { swatch ->
            SwatchTile(
                swatch = swatch,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun SwatchTile(
    swatch: PaletteSwatch,
    modifier: Modifier = Modifier,
) {
    val color = Color(swatch.argb.toInt())
    val labelColor = if (color.luminance() > 0.5f) {
        Color.Black.copy(alpha = 0.78f)
    } else {
        Color.White.copy(alpha = 0.95f)
    }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(color),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Text(
                text = swatch.hex,
                style = MaterialTheme.typography.labelSmall,
                color = labelColor,
                modifier = Modifier.padding(bottom = 8.dp),
            )
        }
    }
}

/**
 * Highlights hue names in the description by tinting them with the dominant
 * (and optional accent) palette swatch — matches the design's blue/purple
 * inline highlights.
 */
private fun highlightHueNames(
    text: String,
    palette: List<PaletteSwatch>,
    dominantLabel: String,
    accentLabel: String?,
    surface: Color,
): AnnotatedString {
    if (palette.isEmpty()) return AnnotatedString(text)
    val highlights = buildList {
        palette.firstOrNull()?.let { add(dominantLabel to ensureReadable(Color(it.argb.toInt()), surface)) }
        if (accentLabel != null && palette.size > 1) {
            add(accentLabel to ensureReadable(Color(palette[1].argb.toInt()), surface))
        }
    }
    return buildAnnotatedString {
        var cursor = 0
        while (cursor < text.length) {
            val match = highlights
                .mapNotNull { (label, color) ->
                    val idx = text.indexOf(label, startIndex = cursor, ignoreCase = true)
                    if (idx >= 0) Triple(idx, label, color) else null
                }
                .minByOrNull { it.first }
            if (match == null) {
                append(text.substring(cursor))
                break
            }
            val (idx, label, color) = match
            if (idx > cursor) append(text.substring(cursor, idx))
            withStyle(SpanStyle(color = color)) {
                append(text.substring(idx, idx + label.length))
            }
            cursor = idx + label.length
        }
    }
}

/**
 * Adjusts [color]'s lightness so it meets at least a 4.5:1 WCAG contrast ratio
 * against [background], while preserving its hue. Critical for readability when
 * a dark sapphire/violet swatch is rendered as inline text on a near-black surface.
 */
private fun ensureReadable(color: Color, background: Color): Color {
    val target = 4.5f
    if (contrastRatio(color, background) >= target) return color
    val (h, s, l) = color.toHsl()
    val bgIsDark = background.luminance() < 0.5f
    // Walk lightness toward the side that gives more contrast against the surface.
    var step = if (bgIsDark) 0.05f else -0.05f
    var newL = l
    repeat(18) {
        newL = (newL + step).coerceIn(0f, 1f)
        // Keep some saturation so the color remains identifiable.
        val candidate = hslToColor(h, max(s, 0.55f), newL)
        if (contrastRatio(candidate, background) >= target) return candidate
    }
    return hslToColor(h, max(s, 0.55f), if (bgIsDark) 0.78f else 0.22f)
}

private fun contrastRatio(a: Color, b: Color): Float {
    val la = a.luminance() + 0.05f
    val lb = b.luminance() + 0.05f
    return if (la > lb) la / lb else lb / la
}

private fun Color.toHsl(): Triple<Float, Float, Float> {
    val r = red
    val g = green
    val bl = blue
    val maxC = max(r, max(g, bl))
    val minC = min(r, min(g, bl))
    val l = (maxC + minC) / 2f
    if (maxC == minC) return Triple(0f, 0f, l)
    val d = maxC - minC
    val s = if (l > 0.5f) d / (2f - maxC - minC) else d / (maxC + minC)
    val h = when (maxC) {
        r -> ((g - bl) / d + if (g < bl) 6f else 0f)
        g -> ((bl - r) / d + 2f)
        else -> ((r - g) / d + 4f)
    } * 60f
    return Triple(h, s, l)
}

private fun hslToColor(h: Float, s: Float, l: Float): Color {
    if (s == 0f) return Color(l, l, l)
    val q = if (l < 0.5f) l * (1f + s) else l + s - l * s
    val p = 2f * l - q
    val hk = ((h % 360f) + 360f) % 360f / 360f
    val r = hueToRgb(p, q, hk + 1f / 3f)
    val g = hueToRgb(p, q, hk)
    val b = hueToRgb(p, q, hk - 1f / 3f)
    return Color(r, g, b)
}

private fun hueToRgb(p: Float, q: Float, tIn: Float): Float {
    var t = tIn
    if (t < 0f) t += 1f
    if (t > 1f) t -= 1f
    return when {
        t < 1f / 6f -> p + (q - p) * 6f * t
        t < 1f / 2f -> q
        t < 2f / 3f -> p + (q - p) * (2f / 3f - t) * 6f
        else -> p
    }
}
