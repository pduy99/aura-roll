<!--firebender-plan
name: optimize android photo indexer
overview: Speed up `AndroidPhotoIndexer` by parallelizing per-photo decode+palette work, reducing per-swatch redundancy, decoding to RGB_565, skipping Palette's internal resize, and batching DB inserts.
todos:
  - id: parallelize
    content: "Convert sequential loop to flatMapMerge with per-core concurrency"
  - id: palette-tune
    content: "Use Palette.Builder with resizeBitmapArea(-1), maximumColorCount(8), clearFilters()"
  - id: single-pass
    content: "Collapse swatch analysis into a single pass with cached HSV"
  - id: rgb565
    content: "Decode bitmaps as RGB_565 for palette analysis"
  - id: batch-insert
    content: "Add insertAll to DAO and batch DB writes (~25/flush)"
-->


## Optimize AndroidPhotoIndexer

Target file: [shared/src/androidMain/kotlin/com/helios/auraroll/indexing/AndroidPhotoIndexer.kt](shared/src/androidMain/kotlin/com/helios/auraroll/indexing/AndroidPhotoIndexer.kt)

### Bottlenecks observed
- Sequential `while (cursor.moveToNext())` loop — single-threaded decode + Palette generation; should run in parallel across CPU cores. Bitmap decode (I/O) and Palette quantization (CPU) are the dominant cost.
- `Palette.from(bitmap).generate()` uses defaults: 16 colors and an internal resize to 112x112. We already downsample to ~50px; Palette will upsample/resample again — wasted work.
- Two passes over `palette.swatches` (best swatch + matching population) each allocating a fresh `FloatArray(3)` per swatch and calling `Color.colorToHSV` twice per swatch.
- Per-photo `dao.insert(...)` — many small write transactions.
- `BitmapFactory` uses ARGB_8888 by default; for palette analysis RGB_565 is sufficient and ~2x faster / half memory.

### Changes

1. **Parallel pipeline** — in [AndroidPhotoIndexer.kt](shared/src/androidMain/kotlin/com/helios/auraroll/indexing/AndroidPhotoIndexer.kt):
   - Read all cursor rows into a lightweight `List<RowMeta>` upfront (id, width, height), filtering out `existingIds` immediately.
   - Process with `asFlow().flatMapMerge(concurrency = Runtime.getRuntime().availableProcessors())` doing decode + Palette + analysis off `Dispatchers.IO` for decode and `Dispatchers.Default` for Palette/HSV math (use `withContext`).
   - Maintain an `AtomicInteger processed` and emit progress every N (e.g. 10) completions.

2. **Tune `Palette.Builder`**:
   - `Palette.Builder(bitmap).resizeBitmapArea(-1).maximumColorCount(8).clearFilters().generate()` — skip internal resize (we already downsample), use 8 colors instead of 16, drop default filters that re-run HSV checks.

3. **Single-pass swatch analysis** — replace the two `palette.swatches` passes with one loop that:
   - Pre-computes `Triple(h, s, v, population)` once per swatch.
   - Tracks `bestSwatch` by `s * population` weight.
   - Computes `totalPopulation` simultaneously.
   - Then a single pass for `matchingPopulation` using the cached HSV values.
   - Reuse one `FloatArray(3)` across the loop.

4. **Decode tuning**:
   - Set `inPreferredConfig = Bitmap.Config.RGB_565` in `BitmapFactory.Options`.
   - Drop the fallback `inJustDecodeBounds` probe path when MediaStore dims are valid (already done) — keep as-is.

5. **Batched DB inserts**:
   - Add a `suspend fun insertAll(photos: List<IndexedPhoto>)` to `IndexedPhotoDao` (annotated `@Insert(onConflict = REPLACE)`).
   - In the indexer, buffer results into a list; flush every ~25 photos or when emitting progress, so Room's reactive flows still update the UI smoothly but with far fewer transactions.

### Out of scope / not changing
- Selection criteria, sort order, MediaStore projection.
- `HUE_TOLERANCE_DEGREES`, monochrome thresholds, hue/dominance semantics — only the computation path is restructured; outputs must remain bit-identical for the same input.
- `IndexingState` shape and emission cadence (~every 10 photos).

### Risk / correctness notes
- Parallel inserts: keep DB writes serialized via the batching step (single coroutine drains the buffer) to avoid Room contention.
- Palette `clearFilters()` removes the default `IS_LIGHT_VIGNETTE` etc. filter — our own saturation/brightness gating already excludes neutrals, so this is safe and matches existing intent.
- `RGB_565` may slightly shift quantized hues vs ARGB_8888; given the 30deg `HUE_TOLERANCE_DEGREES` bucketing this is well within tolerance, but worth a sanity check on a few photos after the change.
