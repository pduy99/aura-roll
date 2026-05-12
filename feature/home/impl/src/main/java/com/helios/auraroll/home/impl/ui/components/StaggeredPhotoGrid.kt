package com.helios.auraroll.home.impl.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.helios.auraroll.data.model.Photo
import kotlinx.collections.immutable.ImmutableList

private val photoCornerRadius = RoundedCornerShape(16.dp)
private const val FULL_WIDTH_ASPECT_RATIO_THRESHOLD = 1.5f

/**
 * Two-column staggered masonry grid of indexed photos.
 *
 * Photo items animate in/out and reflow smoothly as the filtered set changes
 * (e.g. when the user drags the hue slider). When [quoteContent] is provided
 * it is injected as a full-width row after the photo at [quoteInsertIndex].
 */
@Composable
fun StaggeredPhotoGrid(
    photos: ImmutableList<Photo>,
    modifier: Modifier = Modifier,
    state: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    quoteInsertIndex: Int = -1,
    quoteContent: (@Composable () -> Unit)? = null,
    headerContent: @Composable (() -> Unit)? = null,
    onPhotoClick: (Photo) -> Unit = {},
) {
    // Reorder photos so that columns are balanced before each full-line (landscape)
    // photo, eliminating the empty-lane gaps that a vertical staggered grid would
    // otherwise leave when a 2-column item lands on uneven columns.
    val anchorPhotoId = remember(photos, quoteInsertIndex) {
        photos.getOrNull(quoteInsertIndex)?.id
    }
    val arrangedPhotos = remember(photos) { arrangePhotosForStaggeredGrid(photos) }
    val effectiveQuoteInsertIndex = remember(arrangedPhotos, anchorPhotoId) {
        anchorPhotoId?.let { id -> arrangedPhotos.indexOfFirst { it.id == id } } ?: -1
    }

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        state = state,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalItemSpacing = 8.dp
    ) {
        if (headerContent != null) {
            item(
                span = StaggeredGridItemSpan.FullLine,
                key = "home-header",
                contentType = "header"
            ) {
                headerContent()
            }
        }

        arrangedPhotos.forEachIndexed { index, photo ->
            val photoSpan = if (photo.aspectRatio >= FULL_WIDTH_ASPECT_RATIO_THRESHOLD) {
                StaggeredGridItemSpan.FullLine
            } else {
                StaggeredGridItemSpan.SingleLane
            }
            item(key = photo.id, contentType = "photo", span = photoSpan) {
                PhotoCard(
                    photo = photo,
                    onClick = { onPhotoClick(photo) },
                    modifier = Modifier.animateItem(
                        fadeInSpec = tween(durationMillis = 350),
                        fadeOutSpec = tween(durationMillis = 250),
                        placementSpec = spring(
                            stiffness = Spring.StiffnessMediumLow,
                            dampingRatio = Spring.DampingRatioLowBouncy
                        )
                    )
                )
            }

            if (quoteContent != null && index == effectiveQuoteInsertIndex) {
                item(
                    key = "home-quote",
                    span = StaggeredGridItemSpan.FullLine,
                    contentType = "quote"
                ) {
                    Box(
                        modifier = Modifier.animateItem(
                            fadeInSpec = tween(durationMillis = 400),
                            fadeOutSpec = tween(durationMillis = 250),
                            placementSpec = spring(
                                stiffness = Spring.StiffnessMediumLow
                            )
                        )
                    ) {
                        quoteContent()
                    }
                }
            }
        }
    }
}

/**
 * Reorders photos so that whenever a full-line (landscape) photo is about to be
 * placed, the two staggered columns are as balanced as possible. When a gap is
 * detected, the next portrait photo whose height best fills the shorter column
 * is pulled forward. Falls back to original order when no suitable filler
 * exists. Heights are computed in column-width units (height = width /
 * aspectRatio) so absolute pixel sizes are not needed.
 */
private fun arrangePhotosForStaggeredGrid(photos: List<Photo>): List<Photo> {
    if (photos.isEmpty()) return photos
    val remaining = ArrayDeque(photos)
    val result = ArrayList<Photo>(photos.size)
    var col1 = 0f
    var col2 = 0f
    val balanceTolerance = 0.05f

    while (remaining.isNotEmpty()) {
        val first = remaining.first()
        val firstIsLandscape = first.aspectRatio >= FULL_WIDTH_ASPECT_RATIO_THRESHOLD
        val gap = kotlin.math.abs(col1 - col2)

        if (firstIsLandscape && gap > balanceTolerance) {
            // Look for the portrait whose height best matches the gap.
            var bestIdx = -1
            var bestDiff = Float.MAX_VALUE
            for (i in remaining.indices) {
                val p = remaining[i]
                if (p.aspectRatio >= FULL_WIDTH_ASPECT_RATIO_THRESHOLD) continue
                val ph = 1f / p.aspectRatio.coerceAtLeast(0.01f)
                val diff = kotlin.math.abs(ph - gap)
                if (diff < bestDiff) {
                    bestDiff = diff
                    bestIdx = i
                }
            }
            if (bestIdx <= 0) {
                // No portrait ahead (or it is already first): just emit the landscape.
                remaining.removeFirst()
                result.add(first)
                val lh = 2f / first.aspectRatio.coerceAtLeast(0.01f)
                val newH = kotlin.math.max(col1, col2) + lh
                col1 = newH
                col2 = newH
            } else {
                val portrait = remaining.removeAt(bestIdx)
                result.add(portrait)
                val ph = 1f / portrait.aspectRatio.coerceAtLeast(0.01f)
                if (col1 <= col2) col1 += ph else col2 += ph
            }
        } else {
            remaining.removeFirst()
            result.add(first)
            if (firstIsLandscape) {
                val lh = 2f / first.aspectRatio.coerceAtLeast(0.01f)
                val newH = kotlin.math.max(col1, col2) + lh
                col1 = newH
                col2 = newH
            } else {
                val ph = 1f / first.aspectRatio.coerceAtLeast(0.01f)
                if (col1 <= col2) col1 += ph else col2 += ph
            }
        }
    }
    return result
}

@Composable
private fun PhotoCard(
    photo: Photo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val safeAspectRatio = if (photo.aspectRatio > 0f) photo.aspectRatio else 1f

    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(photo.uri)
            .crossfade(true)
            .build(),
        contentDescription = "Photo",
        contentScale = ContentScale.Crop,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(safeAspectRatio)
            .clip(photoCornerRadius)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clickable(role = Role.Image, onClick = onClick)
    )
}
