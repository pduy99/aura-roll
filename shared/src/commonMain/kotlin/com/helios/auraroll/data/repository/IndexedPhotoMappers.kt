package com.helios.auraroll.data.repository

import com.helios.auraroll.data.model.Photo
import com.helios.auraroll.database.IndexedPhoto

internal fun IndexedPhoto.toPhoto(): Photo = Photo(
    id = id,
    uri = uri,
    aspectRatio = aspectRatio,
)
