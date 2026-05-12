package com.helios.auraroll.detail.data.source

import com.helios.auraroll.detail.PhotoExif

/** Reads camera-side EXIF for a photo. Returns an empty record on failure. */
interface PhotoExifSource {
    suspend fun readExif(uri: String): PhotoExif
}
