package com.helios.auraroll.home.impl.ui

import androidx.compose.runtime.Stable
import com.helios.auraroll.data.model.Photo
import com.helios.auraroll.hue.hueToColorName
import com.helios.auraroll.quotes.HueQuote
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

enum class FilterMode { COLOR, MONOCHROME }

private const val DEFAULT_HUE = 198f

@Stable
data class HomeUiState(
    val selectedHue: Float = DEFAULT_HUE,
    val hueLabel: String = hueToColorName(DEFAULT_HUE),
    val filterMode: FilterMode = FilterMode.COLOR,
    val photoCount: Int = 0,
    val photos: ImmutableList<Photo> = persistentListOf(),
    val isLoading: Boolean = true,
    val quote: HueQuote? = null,
    val quoteInsertIndex: Int = -1,
    val effect: HomeEffect? = null,
)

sealed interface HomeAction {
    data class HueChanged(val hue: Float) : HomeAction
    data class FilterModeChanged(val mode: FilterMode) : HomeAction
    data object ConsumeEffect : HomeAction
}

sealed interface HomeEffect
