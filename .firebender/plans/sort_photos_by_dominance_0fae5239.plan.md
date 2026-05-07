<!--firebender-plan
name: Sort Photos By Dominance
overview: Order hue-filtered photos by colorDominance descending so the "purest" matches surface first. Done in SQL at the DAO layer (not the ViewModel) for performance and correctness with the wrap-around merge.
todos:
  - id: dao-order-by
    content: "Change observePhotosByHueRange ORDER BY to colorDominance DESC, id DESC"
  - id: repo-merge-sort
    content: "Re-sort the wrap-around merged list in OfflineFirstIndexedPhotoRepository by the same key"
-->

# Sort Photos By Dominance

## Why DAO, not ViewModel

- Room already runs the query — adding `ORDER BY colorDominance DESC` is one SQL token, executed in C, with no Kotlin allocation.
- Sorting in the ViewModel would re-sort the entire list every time the Room flow emits (which happens after every per-photo insert during indexing, potentially thousands of times).
- The ViewModel collects from a `combine` block; doing the sort there couples ordering to the UI layer instead of keeping it a property of the data layer.

## Changes

### 1. [`IndexedPhotoDao.kt`](shared/src/commonMain/kotlin/com/helios/auraroll/database/IndexedPhotoDao.kt)

Change the photo-fetch query's ordering. The count query is unaffected.

```kotlin
@Query("""
    SELECT * FROM indexed_photos
    WHERE hue >= :minHue AND hue <= :maxHue
      AND isMonochrome = 0
      AND colorDominance >= :minDominance
    ORDER BY colorDominance DESC, id DESC
""")
fun observePhotosByHueRange(minHue: Float, maxHue: Float, minDominance: Float): Flow<List<IndexedPhoto>>
```

Tiebreaker on `id DESC` keeps recency as a secondary sort when two photos share the same dominance value (rare, but well-defined).

### 2. [`OfflineFirstIndexedPhotoRepository.kt`](shared/src/commonMain/kotlin/com/helios/auraroll/data/repository/OfflineFirstIndexedPhotoRepository.kt)

The wrap-around case (red at 0°/360°) merges two SQL queries — each pre-sorted, but the merged list isn't. Re-sort the merged result by the same key:

```kotlin
combine(
    dao.observePhotosByHueRange(min, 360f, minDominance),
    dao.observePhotosByHueRange(0f, max, minDominance),
) { upper, lower ->
    (upper + lower).sortedWith(
        compareByDescending<IndexedPhoto> { it.colorDominance }.thenByDescending { it.id }
    )
}
```

### 3. Monochrome list — leave as-is

`observeMonochromePhotos()` keeps `ORDER BY id DESC`. Dominance is not meaningful for monochrome photos (there's no "selected hue" to be dominated by).

## Out of scope

- ViewModel changes — none needed; it just consumes the now-pre-sorted flow.
- No database schema changes, no migration.
