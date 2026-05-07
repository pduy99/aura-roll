package com.helios.auraroll.home.impl.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.helios.auraroll.home.impl.ui.FilterMode

@Composable
fun HueHeader(
    hueLabel: String,
    photoCount: Int,
    hueColor: Color,
    filterMode: FilterMode,
    modifier: Modifier = Modifier
) {
    val isMonochrome = filterMode == FilterMode.MONOCHROME
    val title = if (isMonochrome) "Monochrome" else hueLabel
    val subtitle = if (isMonochrome) {
        "$photoCount PHOTOGRAPHS IN MONOCHROME"
    } else {
        "$photoCount PHOTOGRAPHS FILTERED BY HUE"
    }
    val dotColor = if (isMonochrome) MaterialTheme.colorScheme.onBackground else hueColor

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            // Mode indicator dot
            Spacer(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .drawBehind { drawRect(dotColor) }
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.8.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
