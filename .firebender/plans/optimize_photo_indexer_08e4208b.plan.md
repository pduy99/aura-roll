<!--firebender-plan
name: Optimize Photo Indexer
overview: Optimize AndroidPhotoIndexer along four axes: batched DB inserts, single-pass bitmap I/O using MediaStore dimensions, removal of the unused in-loop palette-color sampling, and structured logging.
todos:
  - id: dao-insert-all
    content: "Add insertAll(photos: List<IndexedPhoto>) to IndexedPhotoDao"
  - id: indexer-batch
    content: "Batch IndexedPhoto inserts in AndroidPhotoIndexer (size 50, flush on completion)"
  - id: indexer-cursor-dims
    content: "Read WIDTH/HEIGHT from MediaStore cursor; remove inJustDecodeBounds pass; fallback for invalid dims"
  - id: remove-detected-colors
    content: "Remove detectedPaletteColors field from IndexingState and the in-loop accumulator"
  - id: structured-logging
    content: "Replace printStackTrace with Log.e and TAG constant"
-->

# Optimize Photo Indexer

Targets the four highest-impact issues from the review of [`AndroidPhotoIndexer.kt`](shared/src/androidMain/kotlin/com/helios/auraroll/indexing/AndroidPhotoIndexer.kt). Skips API-29-only `loadThumbnail` (project's `minSdk = 28`) and skips the `LongHashSet` micro-optimization (only matters at 100k+ photos).

---

## Fix 1 — Batch DB inserts

**Problem**: `dao.insert(indexedPhoto)` runs once per photo (line 157) — each is its own SQLite transaction with its own `fsync`. For 10k photos this dominates indexing time.

**Changes**:

- [`IndexedPhotoDao.kt`](shared/src/commonMain/kotlin/com/helios/auraroll/database/IndexedPhotoDao.kt): add a list-insert method.
  ```kotlin
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(photos: List<IndexedPhoto>)
  ```
- [`AndroidPhotoIndexer.kt`](shared/src/androidMain/kotlin/com/helios/auraroll/indexing/AndroidPhotoIndexer.kt):
  - Accumulate processed photos in a `MutableList<IndexedPhoto>` of capacity `BATCH_SIZE = 50`.
  - Flush via `dao.insertAll(buffer); buffer.clear()` whenever it reaches `BATCH_SIZE`, after the cursor loop ends, and inside any early-exit path.
  - Remove the per-photo `dao.insert(indexedPhoto)` call.

---

## Fix 2 — Single-pass bitmap decode using MediaStore dimensions

**Problem**: lines 84–106 open the same `InputStream` twice per photo — once with `inJustDecodeBounds = true` to learn dimensions, again to decode pixels.

**Changes** in [`AndroidPhotoIndexer.kt`](shared/src/androidMain/kotlin/com/helios/auraroll/indexing/AndroidPhotoIndexer.kt):

- Extend cursor projection with `WIDTH` + `HEIGHT`:
  ```kotlin
  val projection = arrayOf(
      MediaStore.Images.Media._ID,
      MediaStore.Images.Media.SIZE,
      MediaStore.Images.Media.WIDTH,
      MediaStore.Images.Media.HEIGHT,
  )
  ```
- Cache the `WIDTH` / `HEIGHT` column indexes outside the loop (after `idColumn`).
- Read `width` / `height` from the cursor for each row; compute `aspectRatio` and `inSampleSize` from those values directly (no `inJustDecodeBounds` pass).
- Open the input stream **only once** per photo to do the actual downsampled decode.
- **Fallback**: if cursor `width <= 0 || height <= 0` (corrupted MediaStore row), fall back to the existing `inJustDecodeBounds` path so we don't lose those photos.
- The existing `calculateInSampleSize` helper continues to work — just feed it cursor dimensions wrapped in a small `BitmapFactory.Options` (or refactor to take raw width/height ints).

Net effect: one stream open per photo instead of two.

---

## Fix 3 — Drop unused in-loop palette sampling (review item 6)

**Problem**: `detectedColors` set is capped at 5 entries from the newest photos and is never consumed by the UI. The onboarding screen already pulls colors via `IndexedPhotoRepository.observeSampleColors()` → `dao.sampleColors()`. Verified: no callers reference `IndexingState.detectedPaletteColors` outside `IndexingState.kt` and `AndroidPhotoIndexer.kt`.

**Changes**:

- [`IndexingState.kt`](shared/src/commonMain/kotlin/com/helios/auraroll/indexing/IndexingState.kt): remove the `detectedPaletteColors` field.
- [`AndroidPhotoIndexer.kt`](shared/src/androidMain/kotlin/com/helios/auraroll/indexing/AndroidPhotoIndexer.kt): remove the `detectedColors` accumulator, the `< 5` guard, and the empty-list defaults at lines 43, 71, 179, 186.

UI continues to get its sample colors from the live `dao.sampleColors()` flow already wired into `OnboardingViewModel`.

---

## Fix 4 — Structured logging (review item 5, included since it's tiny)

[`AndroidPhotoIndexer.kt`](shared/src/androidMain/kotlin/com/helios/auraroll/indexing/AndroidPhotoIndexer.kt): replace `t.printStackTrace()` with `Log.e(TAG, "Failed to index $contentUri", t)` and add a `companion object { private const val TAG = "AndroidPhotoIndexer" }`. Keep the broad `catch (t: Throwable)` since it intentionally guards against `OutOfMemoryError` from the bitmap decode.

---

## Out of scope

- `ContentResolver.loadThumbnail` (needs `minSdk = 29`, project is on 28).
- `androidx.collection.LongHashSet` for `existingIds` (premature; revisit only if startup memory regresses).
- DB schema migration: `IndexedPhoto` schema is unchanged, so no migration concerns.
