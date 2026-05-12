package com.helios.auraroll.detail

/**
 * One swatch in the editorial palette extracted from a photo.
 *
 * @property argb packed ARGB color, kept as `Long` so the model is KMP-pure.
 * @property hex display string in `#RRGGBB` form.
 * @property population relative weight of this swatch in the source image (0..1).
 */
data class PaletteSwatch(
    val argb: Long,
    val hex: String,
    val population: Float,
)

/** Camera-side EXIF metadata. All fields nullable — EXIF is best-effort. */
data class PhotoExif(
    val camera: String? = null,
    val lens: String? = null,
    val aperture: String? = null,
    val exposure: String? = null,
    val iso: String? = null,
    val focalLength: String? = null,
)

/**
 * 5-bin luminance distribution from shadows (bin 0) to highlights (bin 4).
 *
 * Bin values are normalized 0..1 against the largest bin so the histogram is
 * trivially renderable as bar heights. [tintArgb] supplies the bar tint sampled
 * from the photo's palette so the chart visually matches the image.
 */
data class LuminanceHistogram(
    val bins: List<Float>,
    val tintArgb: List<Long>,
    val peakBin: Int,
) {
    val caption: String
        get() = when (peakBin) {
            0, 1 -> "Luminance distribution favors shadow tones."
            2 -> "Luminance distribution favors high-mid tones."
            else -> "Luminance distribution favors highlights."
        }
}

/**
 * Aggregated, UI-ready detail of a single photo. Built once on demand and held
 * by the Detail screen's [PhotoDetailUiState].
 */
data class PhotoDetail(
    val id: Long,
    val uri: String,
    val fileName: String,
    val location: String?,
    val aspectRatio: Float,
    val palette: List<PaletteSwatch>,
    val dominantHueLabel: String,
    val accentHueLabel: String?,
    val description: String,
    val temperatureBalance: Float,
    val histogram: LuminanceHistogram,
    val exif: PhotoExif,
)
