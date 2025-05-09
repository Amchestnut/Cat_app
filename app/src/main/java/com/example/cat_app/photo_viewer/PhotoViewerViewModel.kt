package com.example.cat_app.photo_viewer

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import com.example.cat_app.photo_viewer.PhotoViewerContract.UiState
import com.example.cat_app.photo_viewer.PhotoViewerContract.UiEvent


@HiltViewModel
class PhotoViewerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val images: List<String> = savedStateHandle.get<List<String>>("images") ?: emptyList()
    private val startIndex: Int = savedStateHandle.get<Int>("startIndex") ?: 0

    private val _state = MutableStateFlow(
        UiState(
            images = images,
            currentIndex = startIndex
        )
    )
    val state: StateFlow<UiState> = _state.asStateFlow()
    private fun setState(reducer : UiState.() -> UiState) = _state.getAndUpdate(reducer)


    fun setPage(index: Int) {
        setState { copy(currentIndex = index) }
    }
}
