package com.helios.auraroll.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "indexed_photos", indices = [Index("hue")])
data class IndexedPhoto(
    @PrimaryKey val id: Long,       // MediaStore image ID
    val uri: String,
    val hue: Float?,                // 0–360; null = monochrome
    val saturation: Float,
    val brightness: Float,          // HSV value
    val isMonochrome: Boolean,
    val dominantColorArgb: Long,    // packed ARGB for palette swatches
    val aspectRatio: Float = 1f,    // width / height, used by the staggered grid
    val colorDominance: Float = 0f  // 0–1; share of palette population covered by the winning swatch
)
