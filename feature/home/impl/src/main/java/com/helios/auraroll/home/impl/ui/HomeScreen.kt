package com.helios.auraroll.home.impl.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.helios.auraroll.core.designsystem.theme.AuraRollTheme
import com.helios.auraroll.home.impl.ui.components.FilterModeToggle
import com.helios.auraroll.home.impl.ui.components.HomeTopBar
import com.helios.auraroll.home.impl.ui.components.HueHeader
import com.helios.auraroll.home.impl.ui.components.QuoteCard
import com.helios.auraroll.home.impl.ui.components.SpectrumSlider
import com.helios.auraroll.home.impl.ui.components.StaggeredPhotoGrid
import com.helios.auraroll.home.impl.utils.hueToColor
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onAction: (HomeAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val hueColor = remember(uiState.selectedHue) { hueToColor(uiState.selectedHue) }

    // Subtle animated tint that breathes with the selected hue.
    val backgroundTint by animateColorAsState(
        targetValue = hueColor.copy(alpha = 0.04f),
        animationSpec = tween(durationMillis = 600),
        label = "backgroundTint"
    )

    val gridState = rememberLazyStaggeredGridState()
    val scope = rememberCoroutineScope()

    // Drive slider visibility from scroll direction: visible when at top or scrolling up.
    // Only react to actual user scrolling so that grid reflows triggered by hue/filter
    // changes do not flicker the slider.
    var isSliderVisible by remember { mutableStateOf(true) }
    LaunchedEffect(gridState) {
        var previousIndex = gridState.firstVisibleItemIndex
        var previousOffset = gridState.firstVisibleItemScrollOffset
        snapshotFlow {
            Triple(
                gridState.isScrollInProgress,
                gridState.firstVisibleItemIndex,
                gridState.firstVisibleItemScrollOffset
            )
        }.collectLatest { (scrolling, currentIndex, currentOffset) ->
            val atTop = currentIndex == 0 && currentOffset == 0
            if (!scrolling) {
                previousIndex = currentIndex
                previousOffset = currentOffset
                if (atTop) isSliderVisible = true
                return@collectLatest
            }
            val scrollingUp = when {
                currentIndex < previousIndex -> true
                currentIndex > previousIndex -> false
                else -> currentOffset < previousOffset
            }
            previousIndex = currentIndex
            previousOffset = currentOffset
            isSliderVisible = atTop || scrollingUp
        }
    }

    val isAtTop by remember {
        derivedStateOf {
            gridState.firstVisibleItemIndex == 0 && gridState.firstVisibleItemScrollOffset == 0
        }
    }
    val isMonochrome = uiState.filterMode == FilterMode.MONOCHROME
    val isFabVisible = if (isMonochrome) !isAtTop else !isSliderVisible

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Animated hue aura layer at low opacity so photos always win.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundTint)
        )

        StaggeredPhotoGrid(
            photos = uiState.photos,
            state = gridState,
            modifier = Modifier.fillMaxSize(),
            quoteInsertIndex = uiState.quoteInsertIndex,
            quoteContent = uiState.quote?.let { quote ->
                { QuoteCard(quote = quote, hueColor = hueColor) }
            },
            headerContent = {
                Column {
                    HomeTopBar()

                    Spacer(modifier = Modifier.height(16.dp))

                    HueHeader(
                        hueLabel = uiState.hueLabel,
                        photoCount = uiState.photoCount,
                        hueColor = hueColor,
                        filterMode = uiState.filterMode
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    FilterModeToggle(
                        selected = uiState.filterMode,
                        onModeSelected = { onAction(HomeAction.FilterModeChanged(it)) }
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        )

        // Floating hue slider that auto-hides on scroll-down. Hidden in monochrome mode.
        AnimatedVisibility(
            visible = isSliderVisible && uiState.filterMode == FilterMode.COLOR,
            enter = slideInVertically(
                animationSpec = tween(durationMillis = 280),
                initialOffsetY = { it }
            ) + fadeIn(animationSpec = tween(220)),
            exit = slideOutVertically(
                animationSpec = tween(durationMillis = 240),
                targetOffsetY = { it }
            ) + fadeOut(animationSpec = tween(180)),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            SpectrumSlider(
                selectedHue = uiState.selectedHue,
                onHueChanged = { onAction(HomeAction.HueChanged(it)) }
            )
        }

        // Scroll-to-top FAB: shown when slider is hidden (color mode) or whenever
        // the grid is scrolled (monochrome mode, where the slider is always hidden).
        AnimatedVisibility(
            visible = isFabVisible,
            enter = slideInVertically(
                animationSpec = tween(durationMillis = 280),
                initialOffsetY = { it }
            ) + fadeIn(animationSpec = tween(220)),
            exit = slideOutVertically(
                animationSpec = tween(durationMillis = 240),
                targetOffsetY = { it }
            ) + fadeOut(animationSpec = tween(180)),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 24.dp)
        ) {
            SmallFloatingActionButton(
                onClick = { scope.launch { gridState.animateScrollToItem(0) } },
                containerColor = hueColor.copy(alpha = 0.9f),
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowUp,
                    contentDescription = "Scroll to top"
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    AuraRollTheme {
        HomeScreen(
            uiState = HomeUiState(
                selectedHue = 198f,
                hueLabel = "Oceanic Cyan",
                photoCount = 124
            ),
            onAction = {}
        )
    }
}
