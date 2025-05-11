package com.example.cat_app.photo_viewer

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import com.example.cat_app.photo_viewer.PhotoViewerContract.UiState
import com.example.cat_app.photo_viewer.PhotoViewerContract.UiEvent
import com.example.cat_app.repository.BreedImagesRepository
import kotlinx.coroutines.launch


@HiltViewModel
class PhotoViewerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: BreedImagesRepository,
) : ViewModel() {
    // samo za debugging trenutno
    private val images: List<String> = savedStateHandle.get<List<String>>("images") ?: emptyList()

    private val breedId: String = savedStateHandle.get<String>("breedId") ?: throw IllegalArgumentException("breedId missing")
    private val startIndex: Int = savedStateHandle.get<Int>("startIndex") ?: 0

    private val _state = MutableStateFlow(
        UiState(
            images = emptyList(),
            currentIndex = startIndex,
        )
    )
    val state: StateFlow<UiState> = _state.asStateFlow()
    private fun setState(reducer : UiState.() -> UiState) = _state.getAndUpdate(reducer)

    init {
        Log.d("PhotoViewerVM", "init: images.size=${images.size}, startIndex=$startIndex")
        loadImages()
    }

    private fun loadImages() = viewModelScope.launch {
        try {
            repository.fetchAndCacheBreedImages(breedId)
            val entities = repository.getAllImagesForThisBreed(breedId).first()
            val urls = entities.map {
                it.url
            }
            Log.d("PhotoViewerVM", "Loaded ${urls.size} images for $breedId")
            setState { copy(images = urls, loading = false) }
        }
        catch (t: Throwable) {
            Log.e("PhotoViewerVM", "Error loading images", t)
            setState { copy(error = t.toString(), loading = false) }
        }
    }

    fun setPage(index: Int) {
        Log.d("PhotoViewerVM", "setPage($index)")
        setState { copy(currentIndex = index) }
    }
}
