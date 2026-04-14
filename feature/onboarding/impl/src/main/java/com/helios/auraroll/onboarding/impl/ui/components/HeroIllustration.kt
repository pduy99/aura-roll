package com.helios.auraroll.onboarding.impl.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.helios.auraroll.core.designsystem.theme.AuraRollTheme
import com.helios.auraroll.core.designsystem.theme.OutlineVariant
import com.helios.auraroll.core.designsystem.theme.SurfaceContainerHighest
import com.helios.auraroll.core.designsystem.theme.SurfaceContainerLow
import com.helios.auraroll.onboarding.impl.R

@Composable
fun HeroIllustration(modifier: Modifier = Modifier) {
    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        val scale by remember { derivedStateOf { minOf(maxWidth, maxHeight).value / 300f } }

        val containerWidth = (200 * scale).dp
        val containerHeight = (280 * scale).dp
        val containerCornerRadius = (32 * scale).dp

        val cardWidth = (75 * scale).dp
        val cardHeight = (105 * scale).dp
        val cardCornerRadius = (16 * scale).dp

        val swirlSize = (110 * scale).dp
        val borderWidth = remember { maxOf(1f, 1 * scale).dp }

        Box(
            modifier = Modifier
                .size(containerWidth, containerHeight)
                .clip(RoundedCornerShape(containerCornerRadius))
                .background(SurfaceContainerLow.copy(alpha = 0.5f))
                .border(
                    borderWidth,
                    OutlineVariant.copy(alpha = 0.05f),
                    RoundedCornerShape(containerCornerRadius)
                )
        )

        // Left camera card
        Image(
            painter = painterResource(id = R.drawable.img_hero_camera),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .offset { IntOffset((-35 * scale).dp.roundToPx(), (-50 * scale).dp.roundToPx()) }
                .rotate(-12f)
                .size(width = cardWidth, height = cardHeight)
                .clip(RoundedCornerShape(cardCornerRadius))
                .background(SurfaceContainerHighest)
                .border(
                    borderWidth,
                    OutlineVariant.copy(alpha = 0.1f),
                    RoundedCornerShape(cardCornerRadius)
                )
        )

        // Right photo card
        Image(
            painter = painterResource(id = R.drawable.img_hero_photo),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .offset { IntOffset((45  * scale).dp.roundToPx(), (-25 * scale).dp.roundToPx()) }
                .rotate(5f)
                .size(width = cardWidth, height = cardHeight)
                .clip(RoundedCornerShape(cardCornerRadius))
                .border(
                    borderWidth,
                    OutlineVariant.copy(alpha = 0.1f),
                    RoundedCornerShape(cardCornerRadius)
                )
        )

        // Bottom swirl circle
        Image(
            painter = painterResource(id = R.drawable.img_hero_swirl),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .offset { IntOffset(0, (75 * scale).dp.roundToPx()) }
                .size(swirlSize)
                .clip(CircleShape)
                .background(Color.Black)
                .border(maxOf(1f, 2 * scale).dp, SurfaceContainerHighest, CircleShape)
        )
    }
}

@Preview
@Composable
private fun HeroIllustrationPreview() {
    AuraRollTheme {
        HeroIllustration()
    }
}

