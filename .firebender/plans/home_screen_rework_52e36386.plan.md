<!--firebender-plan
name: home screen rework
overview: Rework HomeScreen to match the Figma: centered title top bar with left hamburger, removed bottom nav, a compact floating HUE slider that auto-hides on scroll, and animated photo enter/exit transitions when the hue filter changes.
todos:
  - id: topbar
    content: "Rework HomeTopBar: left hamburger, centered AURA ROLL title"
  - id: remove-bottom
    content: "Remove HomeBottomBar from HomeScreen scaffold"
  - id: slider
    content: "Redesign SpectrumSlider to compact pill (icon + gradient + HUE label)"
  - id: auto-hide
    content: "Add scroll-direction-driven AnimatedVisibility floating slider"
  - id: photo-anim
    content: "Add animateItem fade/placement + scale-in to photo grid items"
-->

# Home Screen Rework

## 1. Top bar — `HomeTopBar.kt`
- Replace current layout with: hamburger `Icons.Outlined.Menu` on the **left**, centered `Text("AURA ROLL")` (uppercase, letter-spaced), no right icon.
- Use `Box` with `Alignment.Center` for the title and `Alignment.CenterStart` for the menu button so the title stays optically centered regardless of icon size.
- Remove the `CalendarViewMonth` icon and the right-side `Menu` icon.

## 2. Remove bottom navigation — `HomeScreen.kt`
- Delete the `HomeBottomBar` from the `Scaffold.bottomBar`.
- Drop import of `HomeBottomBar`. (Leave the file in place for now; not referenced elsewhere.)

## 3. Compact floating Hue slider — rewrite `SpectrumSlider.kt`
Match Figma: a single rounded pill containing
- left: filter/tune icon (`Icons.Outlined.Tune`)
- center: gradient hue track + draggable thumb (reuse existing gesture + Canvas track/thumb logic)
- right: `Text("HUE")` label

Remove the "HUE SPECTRUM / CURRENT: …°" labels row, the top-only rounded corners, and the full-width edge-to-edge background. Use `RoundedCornerShape(28.dp)`, `surfaceContainer.copy(alpha = 0.92f)`, horizontal padding `16.dp`, vertical `10.dp`. Keep the existing pointerInput drag handling and Canvas thumb rendering.

## 4. Auto-hide-on-scroll behavior — `HomeScreen.kt`
- Hoist a `LazyStaggeredGridState` in `HomeScreen` and pass it into `StaggeredPhotoGrid` (add a `state` parameter to that component).
- Track scroll direction using a `remember { mutableIntStateOf(...) }` for `previousIndex` and `previousScrollOffset`, derive `isSliderVisible` via `derivedStateOf` (visible when scrolling **up** or at top of list; hidden when scrolling **down**).
- Replace the `Scaffold.bottomBar` with a `Box` overlay anchored to `Alignment.BottomCenter` containing an `AnimatedVisibility` wrapping `SpectrumSlider`, with `slideInVertically { it } + fadeIn()` enter and `slideOutVertically { it } + fadeOut()` exit, padded with `navigationBarsPadding()` and 16.dp horizontal margins so it floats above content.
- Remove `Scaffold` (or keep with `bottomBar = {}`) — simpler to use a single `Box` containing the grid + floating slider + tinted background layer.

## 5. Photo enter/exit animations on filter changes — `StaggeredPhotoGrid.kt`
Use Compose's lazy item animations to make items elegantly enter and leave when the filtered photo list changes:

```kotlin
items(photos, key = { it.id }) { photo ->
    PhotoCard(
        photo = photo,
        modifier = Modifier.animateItem(
            fadeInSpec = tween(350),
            fadeOutSpec = tween(250),
            placementSpec = spring(stiffness = Spring.StiffnessMediumLow)
        )
    )
}
```

This gives a polished fade + reflow when items appear/disappear/reorder as the user drags the hue slider. Requires Compose Foundation 1.7+ (project already uses staggered grid `items`; verify and bump if needed during implementation).

For an extra "impressive" touch, wrap the `AsyncImage` inside `PhotoCard` with a subtle scale-in on first composition using `AnimatedVisibility(visibleState = remember { MutableTransitionState(false).apply { targetState = true } })` with `scaleIn(initialScale = 0.92f) + fadeIn()`.

## 6. Files touched
- [feature/home/impl/.../ui/HomeScreen.kt](feature/home/impl/src/main/java/com/helios/auraroll/home/impl/ui/HomeScreen.kt)
- [feature/home/impl/.../components/HomeTopBar.kt](feature/home/impl/src/main/java/com/helios/auraroll/home/impl/ui/components/HomeTopBar.kt)
- [feature/home/impl/.../components/SpectrumSlider.kt](feature/home/impl/src/main/java/com/helios/auraroll/home/impl/ui/components/SpectrumSlider.kt)
- [feature/home/impl/.../components/StaggeredPhotoGrid.kt](feature/home/impl/src/main/java/com/helios/auraroll/home/impl/ui/components/StaggeredPhotoGrid.kt)

## Out of scope
- `HomeBottomBar.kt` is left on disk but unused (can be deleted in a follow-up).
- `HueHeader` / `FilterModeToggle` are kept as-is (already match the design's title block + Monochrome pill).
