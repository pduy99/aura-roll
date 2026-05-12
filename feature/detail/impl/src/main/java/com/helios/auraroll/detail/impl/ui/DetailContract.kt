package com.helios.auraroll.detail.impl.ui

import androidx.compose.runtime.Stable
import com.helios.auraroll.detail.PhotoDetail

@Stable
data class DetailUiState(
    val photoId: Long = 0L,
    val isLoading: Boolean = true,
    val detail: PhotoDetail? = null,
    val errorMessage: String? = null,
    val effect: DetailEffect? = null,
)

sealed interface DetailAction {
    data object BackClicked : DetailAction
    data object ShareClicked : DetailAction
    data object MoreClicked : DetailAction
    data object ExportPaletteClicked : DetailAction
    data object ConsumeEffect : DetailAction
}

sealed interface DetailEffect {
    /** Open the system share sheet with the formatted palette payload. */
    data class SharePalette(val title: String, val text: String) : DetailEffect

    /** Pop the detail screen off the back stack. */
    data object NavigateBack : DetailEffect
}
