<!--firebender-plan
name: filter mode toggle redesign
overview: Rework `FilterModeToggle` into a single dark pill container with an animated sliding violet thumb between two equal-width segments ("SPECTRUM" / "MONOCHROME"), matching the attached design.
-->

## Scope
Single file change: [feature/home/impl/src/main/java/com/helios/auraroll/home/impl/ui/components/FilterModeToggle.kt](feature/home/impl/src/main/java/com/helios/auraroll/home/impl/ui/components/FilterModeToggle.kt). No enum/contract changes.

## Design spec (from screenshot)
- Outer pill: dark gray (`surfaceVariant` / ~#2A2A2A), full rounded (`RoundedCornerShape(50)`), inner padding ~4dp.
- Two equal-width segments laid out in a `Row` with `Modifier.weight(1f)`.
- Animated thumb: violet (`MaterialTheme.colorScheme.primary`), full rounded, fills exactly one half of the inner area, slides between left/right.
- Text: bold uppercase `labelMedium`. Active segment: `onPrimary` (white). Inactive: `onSurfaceVariant` (gray).
- "COLOR" label becomes "SPECTRUM" (display only; enum stays `FilterMode.COLOR`).

## Implementation approach
Use a `Box` with the outer pill background, containing:
1. An animated thumb `Box` whose horizontal offset is driven by `animateFloatAsState` (fraction 0f -> 1f, target = `if (selected == COLOR) 0f else 1f`). Use `BoxWithConstraints` to compute thumb width = `maxWidth / 2 - innerPadding`, and apply `Modifier.offset { IntOffset((fraction * thumbWidthPx).toInt(), 0) }`.
2. A `Row` of two `FilterModeTab` items on top of the thumb. Each tab uses `Modifier.weight(1f)`, transparent background, text color animated via `animateColorAsState` for smooth crossfade between active/inactive states.
3. Spring animation: `spring(dampingRatio = DampingRatioMediumBouncy, stiffness = StiffnessMediumLow)` for thumb; `tween(250)` for text color.

## Pseudocode
```kotlin
val fraction by animateFloatAsState(
    targetValue = if (selected == FilterMode.COLOR) 0f else 1f,
    animationSpec = spring(dampingRatio = 0.75f, stiffness = Spring.StiffnessMediumLow),
    label = "thumbOffset"
)

BoxWithConstraints(
    modifier = modifier
        .clip(pillShape)
        .background(MaterialTheme.colorScheme.surfaceVariant)
        .padding(4.dp)
) {
    val thumbWidth = maxWidth / 2
    Box(
        Modifier
            .offset { IntOffset((fraction * thumbWidth.toPx()).toInt(), 0) }
            .width(thumbWidth)
            .fillMaxHeight()
            .clip(pillShape)
            .background(MaterialTheme.colorScheme.primary)
    )
    Row(Modifier.fillMaxWidth()) {
        FilterModeTab("SPECTRUM", selected == FilterMode.COLOR, Modifier.weight(1f)) { ... }
        FilterModeTab("MONOCHROME", selected == FilterMode.MONOCHROME, Modifier.weight(1f)) { ... }
    }
}
```

`FilterModeTab` becomes a centered, transparent, clickable `Box` with `animateColorAsState` text color, vertical padding ~10dp.

## Notes
- Remove the outer `Modifier.padding(horizontal = 20.dp)` from internal layout; let the caller (`HomeScreen`) keep controlling outer padding (already does).
- Keep `interactionSource` + `indication = null` for ripple-less taps (matches current behavior).
