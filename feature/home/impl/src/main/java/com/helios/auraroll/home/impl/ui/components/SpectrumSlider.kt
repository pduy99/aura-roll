package com.helios.auraroll.home.impl.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Full 360 degree hue spectrum colors for the slider track. */
private val spectrumColors = listOf(
    Color(0xFFFF0000),
    Color(0xFFFFFF00),
    Color(0xFF00FF00),
    Color(0xFF00FFFF),
    Color(0xFF0000FF),
    Color(0xFFFF00FF),
    Color(0xFFFF0000)
)

/**
 * Compact floating hue selector pill: filter icon, gradient track, "HUE" label.
 */
@Composable
fun SpectrumSlider(
    selectedHue: Float,
    onHueChanged: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.92f))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.Tune,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(22.dp)
        )

        Spacer(modifier = Modifier.width(14.dp))

        HueTrack(
            selectedHue = selectedHue,
            onHueChanged = onHueChanged,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(14.dp))

        Text(
            text = "HUE",
            style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.5.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun HueTrack(
    selectedHue: Float,
    onHueChanged: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var trackWidthPx by remember { mutableFloatStateOf(1f) }
    val thumbRadiusDp = 10.dp
    val trackHeightDp = 6.dp

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(thumbRadiusDp * 2)
            .onSizeChanged { trackWidthPx = it.width.toFloat() }
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown()
                    val hue = (down.position.x / trackWidthPx * 360f).coerceIn(0f, 360f)
                    onHueChanged(hue)
                    do {
                        val event = awaitPointerEvent()
                        val drag = event.changes.firstOrNull() ?: break
                        drag.consume()
                        val dragHue = (drag.position.x / trackWidthPx * 360f).coerceIn(0f, 360f)
                        onHueChanged(dragHue)
                    } while (event.changes.any { it.pressed })
                }
            },
        contentAlignment = Alignment.CenterStart
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(trackHeightDp)
                .align(Alignment.Center)
        ) {
            drawRoundRect(
                brush = Brush.horizontalGradient(spectrumColors),
                cornerRadius = CornerRadius(size.height / 2f)
            )
        }

        val thumbFraction = selectedHue / 360f
        Canvas(modifier = Modifier.fillMaxWidth().height(thumbRadiusDp * 2)) {
            val cx = thumbFraction * size.width
            val cy = size.height / 2f
            val r = thumbRadiusDp.toPx()

            drawCircle(color = Color.White, radius = r, center = Offset(cx, cy))
            drawCircle(
                color = Color.Black.copy(alpha = 0.25f),
                radius = r,
                center = Offset(cx, cy),
                style = Stroke(width = 1.5.dp.toPx())
            )
        }
    }
}
