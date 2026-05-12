package com.helios.auraroll.detail.impl.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.helios.auraroll.domain.usecase.LoadPhotoDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Holds the [DetailUiState] for a single photo.
 *
 * The target photo id is supplied by the caller via [load] rather than
 * SavedStateHandle so the screen plays nicely with Navigation3's type-safe
 * routes (which deliver args directly to the entry installer).
 */
@HiltViewModel
class DetailViewModel @Inject constructor(
    private val loadPhotoDetail: LoadPhotoDetailUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    val effect: Flow<DetailEffect?> = uiState.map { it.effect }

    fun load(photoId: Long) {
        if (_uiState.value.photoId == photoId && _uiState.value.detail != null) return
        if (_uiState.value.photoId == photoId && _uiState.value.isLoading) return
        viewModelScope.launch {
            _uiState.update {
                it.copy(photoId = photoId, isLoading = true, errorMessage = null, detail = null)
            }
            loadPhotoDetail(photoId)
                .onSuccess { detail ->
                    _uiState.update { it.copy(isLoading = false, detail = detail) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Unable to load photo details.",
                        )
                    }
                }
        }
    }

    fun onAction(action: DetailAction) {
        when (action) {
            DetailAction.BackClicked -> {
                _uiState.update { it.copy(effect = DetailEffect.NavigateBack) }
            }
            DetailAction.ShareClicked, DetailAction.ExportPaletteClicked -> {
                val detail = _uiState.value.detail ?: return
                val hexCodes = detail.palette.joinToString(separator = " ") { it.hex }
                val text = buildString {
                    appendLine("AuraRoll palette · ${detail.fileName}")
                    appendLine(hexCodes)
                    detail.location?.let { appendLine(it) }
                }.trimEnd()
                _uiState.update {
                    it.copy(
                        effect = DetailEffect.SharePalette(
                            title = "Share palette",
                            text = text,
                        )
                    )
                }
            }
            DetailAction.MoreClicked -> {
                // Reserved for a future overflow menu (e.g. open in system viewer).
            }
            DetailAction.ConsumeEffect -> {
                _uiState.update { it.copy(effect = null) }
            }
        }
    }
}
