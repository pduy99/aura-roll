<!--firebender-plan
name: scroll-to-top-fab
overview: Add a "scroll to top" floating action button that appears when the hue slider is hidden (i.e., user scrolled down), and on tap smoothly scrolls the grid to the top, restoring the hue slider.
todos:
  - id: fab
    content: "Add scroll-to-top FAB with animated visibility tied to !isSliderVisible in HomeScreen.kt"
-->

## Goal

When the hue slider is hidden, show a circular FAB above the bottom area; tapping it animates the grid back to the top, which naturally hides the FAB and re-shows the slider.

## File touched

- `feature/home/impl/src/main/java/com/helios/auraroll/home/impl/ui/HomeScreen.kt`

## Changes

1. Hoist a `CoroutineScope` via `rememberCoroutineScope()` so we can call `gridState.animateScrollToItem(0)` from a click handler.

2. Replace the single `AnimatedVisibility` block (lines 120-141) with two mutually exclusive `AnimatedVisibility` containers, both aligned to `Alignment.BottomCenter`:
   - **Slider** (existing): `visible = isSliderVisible`, slides up from bottom, full width, padded `horizontal = 16.dp, vertical = 12.dp`.
   - **Scroll-to-top FAB** (new): `visible = !isSliderVisible`, fade + slide-in from bottom. Uses Material3 `SmallFloatingActionButton` with `Icons.Default.KeyboardArrowUp` (or `Icons.Filled.ArrowUpward`). Tinted with `hueColor` for cohesion (container = `hueColor.copy(alpha = 0.9f)`, content = `MaterialTheme.colorScheme.onPrimary`). Aligned `BottomCenter` with `navigationBarsPadding()` and `padding(bottom = 24.dp)`.
   - On click: `scope.launch { gridState.animateScrollToItem(0) }`. The existing `derivedStateOf` for `isSliderVisible` will flip to true once `firstVisibleItemIndex == 0 && offset == 0`, automatically hiding the FAB and revealing the slider — no extra state needed.

3. Add imports: `androidx.compose.material.icons.Icons`, `androidx.compose.material.icons.filled.KeyboardArrowUp`, `androidx.compose.material3.SmallFloatingActionButton`, `androidx.compose.material3.Icon`, `androidx.compose.runtime.rememberCoroutineScope`, `kotlinx.coroutines.launch`.

## Animation symmetry

Use mirrored tweens so the swap reads as a single element morph:
- FAB enter: `slideInVertically(tween(280)) { it } + fadeIn(tween(220))`
- FAB exit: `slideOutVertically(tween(240)) { it } + fadeOut(tween(180))`

## Notes

- No ViewModel/state changes needed — visibility is purely derived from existing scroll state, satisfying the "hide FAB and show slider after scrolling" requirement automatically.
- No new strings/icons resources needed; uses Material icons already on classpath.
