<!--firebender-plan
name: hue quote card
overview: Add a curated "quote of the hue" card that appears at a random non-bottom position within the photo grid, with a fresh random quote chosen each time the user lands on a new hue family.
todos:
  - id: quotes-data
    content: "Create HueQuotes.kt with curated quote lists per hue family + lookup helpers"
  - id: state
    content: "Extend HomeUiState with quote + quoteInsertIndex"
  - id: vm
    content: "Roll new quote on hue family change and recompute insert index on photos change in HomeViewModel"
  - id: card
    content: "Build QuoteCard composable matching the design"
  - id: grid
    content: "Inject quote item into StaggeredPhotoGrid at the chosen index as FullLine span"
  - id: wire
    content: "Wire quote + index from HomeScreen into the grid"
-->

# Hue Quote Card In Photo Grid

## 1. Quote dataset — new `HueQuotes.kt`
Path: `feature/home/impl/src/main/java/com/helios/auraroll/home/impl/data/HueQuotes.kt`

```kotlin
data class HueQuote(val text: String, val author: String)
```

Map each of the 12 hue-name buckets in [HueUtils.kt](shared/src/commonMain/kotlin/com/helios/auraroll/hue/HueUtils.kt) (`hueToColorName`) to a curated `List<HueQuote>` so every family rotates 3-5 quotes:

- **Volcanic Red / Rose Dusk** — Derek Jarman ("Red protects itself..."), plus 2-3 more (Chesterton, Sand, Kandinsky on red).
- **Desert Amber** (orange) — Goethe ("Orange is red brought nearer to humanity by yellow."), plus Frost, Wyeth.
- **Solar Gold** (yellow) — Van Gogh ("How wonderful yellow is..."), plus Klee, O'Keeffe.
- **Garden Lime / Forest Emerald / Jade Mist** (greens) — Kandinsky ("Absolute green is the most restful..."), plus Monet, Pamuk.
- **Arctic Teal / Oceanic Cyan** (cyan) — Dufy ("Blue is the only color which maintains its own character in all its tones." — already on screen), plus Hokusai, Yves Klein.
- **Sapphire Blue / (deep blue / indigo)** — anonymous indigo line ("Indigo is the color of the midnight sky..."), plus Tagore, Rilke.
- **Twilight Violet / Amethyst Purple** — Alice Walker ("Violet is the color of the transition from the physical to the spiritual..."), plus Whistler, O'Hara.

Public API:
```kotlin
fun quotesForHue(hueLabel: String): List<HueQuote>
fun randomQuoteFor(hueLabel: String, random: Random = Random.Default): HueQuote
```

## 2. State — extend `HomeUiState` in [HomeContract.kt](feature/home/impl/src/main/java/com/helios/auraroll/home/impl/ui/HomeContract.kt)
Add:
```kotlin
val quote: HueQuote? = null,
val quoteInsertIndex: Int = -1, // index within `photos` after which the quote is inserted
```

## 3. ViewModel — re-roll quote on hue family change
In [HomeViewModel.kt](feature/home/impl/src/main/java/com/helios/auraroll/home/impl/ui/HomeViewModel.kt):

- Roll a new quote whenever `hueLabel` changes (i.e. the user crosses a hue family boundary). Slider drags within the same family keep the same quote so the UI doesn't flicker — this is the only sensible interpretation of "on hue change" given continuous slider input.
- Roll a new `quoteInsertIndex` whenever the photos list size changes meaningfully, choosing a random index in the **middle ~60%** so the card never lands at the very top or bottom: `Random.nextInt(from = max(2, size / 4), until = max(3, size - 2))` with size guards.
- Add `private var lastQuoteHueLabel: String? = null` and update it inside `HueChanged`. When the new label differs, set `quote = randomQuoteFor(newLabel)` in the same `_uiState.update { ... }` block.
- In the photos collector, recompute `quoteInsertIndex` from the new list size.

## 4. UI — new `QuoteCard.kt` matching the screenshot
Path: `feature/home/impl/src/main/java/com/helios/auraroll/home/impl/ui/components/QuoteCard.kt`

Layout (full-width, fits within a `StaggeredGridItemSpan.FullLine` slot):
- `Surface` with `RoundedCornerShape(20.dp)` and `MaterialTheme.colorScheme.surfaceContainer`.
- Decorative `"\u275D"` (or stylized "99") rendered in the current `hueColor`, large display typography.
- Quote body in `bodyLarge`, slight italic, `onSurface` color.
- Em-dash + author name in caps, `labelMedium`, `onSurfaceVariant`, letter-spacing 1.5sp.
- Padding `20.dp` all around, internal vertical spacing `12.dp`.

Signature:
```kotlin
@Composable
fun QuoteCard(quote: HueQuote, hueColor: Color, modifier: Modifier = Modifier)
```

## 5. Inject quote into the grid — [StaggeredPhotoGrid.kt](feature/home/impl/src/main/java/com/helios/auraroll/home/impl/ui/components/StaggeredPhotoGrid.kt)

Add optional params:
```kotlin
quoteContent: (@Composable () -> Unit)? = null,
quoteInsertIndex: Int = -1
```

Replace the current `items(photos) { ... }` with a manual loop so we can interleave the quote at the chosen index as a `FullLine` span:

```kotlin
photos.forEachIndexed { index, photo ->
    item(key = photo.id, contentType = "photo") { PhotoCard(photo, Modifier.animateItem(...)) }
    if (quoteContent != null && index == quoteInsertIndex) {
        item(
            key = "quote",
            span = StaggeredGridItemSpan.FullLine,
            contentType = "quote"
        ) {
            Box(Modifier.animateItem(fadeInSpec = tween(400))) { quoteContent() }
        }
    }
}
```

Keep `key = "quote"` stable so the card animates content change (quote text) rather than recreating, and so it slots smoothly when its position re-rolls.

## 6. Wire it up — [HomeScreen.kt](feature/home/impl/src/main/java/com/helios/auraroll/home/impl/ui/HomeScreen.kt)

Pass `quoteContent`/`quoteInsertIndex` to `StaggeredPhotoGrid`:

```kotlin
StaggeredPhotoGrid(
    photos = uiState.photos,
    state = gridState,
    quoteInsertIndex = uiState.quoteInsertIndex,
    quoteContent = uiState.quote?.let { q ->
        { QuoteCard(quote = q, hueColor = hueColor) }
    },
    headerContent = { ... }
)
```

## Files
- New: `feature/home/impl/src/main/java/com/helios/auraroll/home/impl/data/HueQuotes.kt`
- New: `feature/home/impl/src/main/java/com/helios/auraroll/home/impl/ui/components/QuoteCard.kt`
- Edit: `HomeContract.kt`, `HomeViewModel.kt`, `HomeScreen.kt`, `StaggeredPhotoGrid.kt`

## Behavior summary
- Drag slider within Cyan family → same quote, same card position.
- Cross from Cyan → Blue → Violet → fresh random quote each time, position may also shift if photo count changes substantially.
- Quote card never appears as the first item or among the last two items.
