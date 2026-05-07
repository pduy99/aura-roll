package com.helios.auraroll.home.impl.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.helios.auraroll.domain.usecase.ObserveFilteredPhotosUseCase
import com.helios.auraroll.domain.usecase.ObservePhotoCountUseCase
import com.helios.auraroll.hue.hueToColorName
import com.helios.auraroll.quotes.SelectHueQuoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val observeFilteredPhotos: ObserveFilteredPhotosUseCase,
    private val observePhotoCount: ObservePhotoCountUseCase,
    private val selectHueQuote: SelectHueQuoteUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        HomeUiState().let { it.copy(quote = selectHueQuote.pickQuote(it.hueLabel)) }
    )
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val effect: Flow<HomeEffect?> = uiState.map { it.effect }

    private val selectedHue = MutableStateFlow(_uiState.value.selectedHue)
    private val filterMode = MutableStateFlow(_uiState.value.filterMode)

    init {
        combine(selectedHue, filterMode) { hue, mode -> hue to mode }
            .flatMapLatest { (hue, mode) ->
                val isMonochrome = mode == FilterMode.MONOCHROME
                combine(
                    observeFilteredPhotos(hue, isMonochrome),
                    observePhotoCount(hue, isMonochrome)
                ) { photos, count -> photos to count }
            }
            .onEach { (photos, count) ->
                _uiState.update { state ->
                    state.copy(
                        photos = photos.toImmutableList(),
                        photoCount = count,
                        isLoading = false,
                        quoteInsertIndex = selectHueQuote.insertIndex(photos.size, state.quoteInsertIndex)
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun onAction(action: HomeAction) {
        when (action) {
            is HomeAction.HueChanged -> {
                selectedHue.value = action.hue
                _uiState.update { state ->
                    val newLabel = hueToColorName(action.hue)
                    val familyChanged = newLabel != state.hueLabel
                    state.copy(
                        selectedHue = action.hue,
                        hueLabel = newLabel,
                        quote = if (familyChanged) selectHueQuote.pickQuote(newLabel) else state.quote
                    )
                }
            }
            is HomeAction.FilterModeChanged -> {
                filterMode.value = action.mode
                _uiState.update { it.copy(filterMode = action.mode) }
            }
            HomeAction.ConsumeEffect -> {
                _uiState.update { it.copy(effect = null) }
            }
        }
    }
}
