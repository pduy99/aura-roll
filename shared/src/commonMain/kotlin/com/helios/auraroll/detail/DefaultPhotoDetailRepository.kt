package com.helios.auraroll.detail

import com.helios.auraroll.database.IndexedPhotoDao
import com.helios.auraroll.detail.analyzer.LuminanceHistogramAnalyzer
import com.helios.auraroll.detail.analyzer.PaletteHueLabeler
import com.helios.auraroll.detail.data.source.PaletteExtractorSource
import com.helios.auraroll.detail.data.source.PhotoExifSource
import com.helios.auraroll.detail.data.source.PhotoImageSource
import com.helios.auraroll.detail.data.source.PhotoLocationSource
import com.helios.auraroll.detail.data.source.PhotoMediaSource

/**
 * KMP-pure orchestration for the Detail screen.
 *
 * Composes five single-responsibility data sources — local index (Room), media
 * catalogue, reverse geocoder, image bytes, EXIF, and palette extraction —
 * with pure analyzer helpers (histogram, hue labels, temperature) into a
 * single [PhotoDetail] payload.
 *
 * Following Google's data-layer guide: the repository centralises composition
 * and exposes immutable data, while each source owns exactly one origin of
 * data. Adding iOS support is a matter of providing iOS implementations of
 * the source interfaces — no change to this class.
 */
class DefaultPhotoDetailRepository(
    private val indexedPhotoDao: IndexedPhotoDao,
    private val mediaSource: PhotoMediaSource,
    private val locationSource: PhotoLocationSource,
    private val imageSource: PhotoImageSource,
    private val exifSource: PhotoExifSource,
    private val paletteExtractor: PaletteExtractorSource,
) : PhotoDetailRepository {

    override suspend fun loadDetail(photoId: Long): Result<PhotoDetail> = runCatching {
        val indexed = indexedPhotoDao.findById(photoId)
            ?: error("Photo $photoId is not indexed")

        val mediaMeta = mediaSource.queryMeta(photoId)
        val location = mediaMeta?.let { meta ->
            val lat = meta.latitude
            val lng = meta.longitude
            if (lat != null && lng != null) locationSource.reverseGeocode(lat, lng) else null
        }

        val image = imageSource.loadDownsampled(indexed.uri, indexed.aspectRatio)
        val palette = image?.let { paletteExtractor.extract(it, PALETTE_DISPLAY_COUNT) }.orEmpty()
        val histogram = image?.let { LuminanceHistogramAnalyzer.analyze(it, palette) }
            ?: LuminanceHistogramAnalyzer.empty()

        val temperature = TemperatureCalculator.compute(
            PaletteHueLabeler.toTemperatureSamples(palette)
        )
        val (dominantLabel, accentLabel) = PaletteHueLabeler.labels(palette, indexed)
        val description = HueDescription.build(dominantLabel, accentLabel, temperature)
        val exif = exifSource.readExif(indexed.uri)

        PhotoDetail(
            id = indexed.id,
            uri = indexed.uri,
            fileName = mediaMeta?.fileName ?: "Photo $photoId",
            location = location,
            aspectRatio = indexed.aspectRatio,
            palette = palette.ifEmpty { PaletteHueLabeler.fallbackPalette(indexed) },
            dominantHueLabel = dominantLabel,
            accentHueLabel = accentLabel,
            description = description,
            temperatureBalance = temperature,
            histogram = histogram,
            exif = exif,
        )
    }

    private companion object {
        private const val PALETTE_DISPLAY_COUNT = 5
    }
}
