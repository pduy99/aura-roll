package com.helios.auraroll.detail.impl.ui.components

import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.helios.auraroll.detail.LuminanceHistogram
import com.helios.auraroll.detail.PhotoExif

@Composable
fun ProMetadataCard(
    exif: PhotoExif,
    histogram: LuminanceHistogram,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(horizontal = 18.dp, vertical = 18.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = "Pro Metadata",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        Spacer(Modifier.height(18.dp))

        ExifGrid(exif = exif)

        Spacer(Modifier.height(18.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
        Spacer(Modifier.height(18.dp))

        HistogramRow(histogram = histogram)

        Spacer(Modifier.height(8.dp))
        Row {
            Text(
                text = "SHADOWS",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = "HIGHLIGHTS",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(Modifier.height(10.dp))
        Text(
            text = histogram.caption,
            style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ExifGrid(exif: PhotoExif) {
    val rows = listOf(
        listOf("CAMERA" to (exif.camera ?: "—"), "LENS" to (exif.lens ?: "—")),
        listOf("APERTURE" to (exif.aperture ?: "—"), "EXPOSURE" to (exif.exposure ?: "—")),
        listOf("ISO" to (exif.iso ?: "—"), "FOCAL LENGTH" to (exif.focalLength ?: "—")),
    )
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        rows.forEach { row ->
            Row(modifier = Modifier.fillMaxWidth()) {
                row.forEachIndexed { index, (label, value) ->
                    ExifCell(
                        label = label,
                        value = value,
                        modifier = Modifier.weight(1f),
                    )
                    if (index == 0) Spacer(Modifier.width(12.dp))
                }
            }
        }
    }
}

@Composable
private fun ExifCell(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun HistogramRow(histogram: LuminanceHistogram) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(72.dp)) {
            val bins = histogram.bins
            if (bins.isEmpty()) return@Canvas
            val gap = 8.dp.toPx()
            val totalGap = gap * (bins.size - 1)
            val barWidth = (size.width - totalGap) / bins.size
            val maxHeight = size.height
            bins.forEachIndexed { index, value ->
                val tint = histogram.tintArgb.getOrNull(index)?.let { Color(it.toInt()) }
                    ?: Color(0xFF1F1F1F)
                val barHeight = (value.coerceIn(0f, 1f) * maxHeight).coerceAtLeast(6.dp.toPx())
                val left = index * (barWidth + gap)
                val top = maxHeight - barHeight
                drawRoundRect(
                    color = tint,
                    topLeft = Offset(left, top),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(6.dp.toPx()),
                )
            }
        }
    }
}
