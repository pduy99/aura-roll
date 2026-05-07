<!--firebender-plan
name: staggered grid full span
overview: Make wide landscape photos (aspect ratio >= 1.2) span both columns of the staggered grid to match the design.
-->

## Span landscape photos full-width in StaggeredPhotoGrid

### Change

In [feature/home/impl/src/main/java/com/helios/auraroll/home/impl/ui/components/StaggeredPhotoGrid.kt](feature/home/impl/src/main/java/com/helios/auraroll/home/impl/ui/components/StaggeredPhotoGrid.kt), assign `StaggeredGridItemSpan.FullLine` to any photo whose `aspectRatio >= 1.2f`; everything else stays single-column (default `SingleLane`).

Add a private threshold constant and pass `span` to the photo `item { }`:

```kotlin
private const val FULL_WIDTH_ASPECT_RATIO_THRESHOLD = 1.2f

photos.forEachIndexed { index, photo ->
    val photoSpan = if (photo.aspectRatio >= FULL_WIDTH_ASPECT_RATIO_THRESHOLD) {
        StaggeredGridItemSpan.FullLine
    } else {
        StaggeredGridItemSpan.SingleLane
    }
    item(key = photo.id, contentType = "photo", span = photoSpan) {
        PhotoCard(photo = photo, modifier = Modifier.animateItem(/* ... */))
    }
    // existing quote injection unchanged
}
```

### Notes

- `PhotoCard` already uses `Modifier.fillMaxWidth().aspectRatio(photo.aspectRatio)`, so a full-line landscape will naturally render as a tall, full-width hero like the design.
- `contentType = "photo"` is kept identical for both spans so Compose can still recycle the composable; the span is part of the lazy item config and triggers a re-layout when it changes.
- No changes needed to `HomeScreen`, ViewModel, or data layer.
