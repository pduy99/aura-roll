package com.helios.auraroll.home.impl.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.helios.auraroll.home.impl.ui.FilterMode

private val PillShape = RoundedCornerShape(percent = 50)

/**
 * Segmented pill toggle with an animated sliding thumb between SPECTRUM and MONOCHROME.
 */
@Composable
fun FilterModeToggle(
    selected: FilterMode,
    onModeSelected: (FilterMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val targetFraction = if (selected == FilterMode.COLOR) 0f else 1f
    val fraction by animateFloatAsState(
        targetValue = targetFraction,
        animationSpec = spring(
            dampingRatio = 0.75f,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "filterModeThumbOffset"
    )

    BoxWithConstraints(
        modifier = modifier
            .clip(PillShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(4.dp)
    ) {
        val density = LocalDensity.current
        val thumbWidth = maxWidth / 2
        val thumbWidthPx = with(density) { thumbWidth.toPx() }

        // Overlay sized to the Row so the thumb can fillMaxHeight reliably.
        Box(modifier = Modifier.matchParentSize()) {
            Box(
                modifier = Modifier
                    .offset { IntOffset(x = (fraction * thumbWidthPx).toInt(), y = 0) }
                    .width(thumbWidth)
                    .fillMaxHeight()
                    .clip(PillShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterModeTab(
                label = "SPECTRUM",
                isActive = selected == FilterMode.COLOR,
                onClick = { onModeSelected(FilterMode.COLOR) },
                modifier = Modifier.weight(1f)
            )
            FilterModeTab(
                label = "MONOCHROME",
                isActive = selected == FilterMode.MONOCHROME,
                onClick = { onModeSelected(FilterMode.MONOCHROME) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun FilterModeTab(
    label: String,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val textColor by animateColorAsState(
        targetValue = if (isActive) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = tween(durationMillis = 250),
        label = "filterModeTabTextColor"
    )

    Box(
        modifier = modifier
            .clip(PillShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .background(Color.Transparent)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = textColor,
            textAlign = TextAlign.Center
        )
    }
}
