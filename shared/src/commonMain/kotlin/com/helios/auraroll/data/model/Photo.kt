package com.helios.auraroll.data.model

/**
 * Lightweight gallery-facing photo model exposed by the data layer.
 *
 * Only fields the UI actually needs live here; Room entity internals (hue, saturation,
 * dominance, etc.) are intentionally hidden so the UI never depends on the database schema.
 *
 * Treated as stable by Compose because all properties are final primitives/Strings.
 */
data class Photo(
    val id: Long,
    val uri: String,
    val aspectRatio: Float,
)
