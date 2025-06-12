package com.example.cat_app.features.photo_viewer

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
import javax.inject.Inject
import com.example.cat_app.features.photo_viewer.PhotoViewerContract.UiState
import com.example.cat_app.features.photo_viewer.PhotoViewerContract.UiEvent
import com.example.cat_app.features.breedgallery.data.repository.BreedImagesRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch


@HiltViewModel
class PhotoViewerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: BreedImagesRepository,
) : ViewModel() {

    private val images: List<String> = savedStateHandle.get<List<String>>("images") ?: emptyList()
    private val breedId: String = savedStateHandle.get<String>("breedId") ?: throw IllegalArgumentException("breedId missing")
    private val startIndex: Int = savedStateHandle.get<Int>("startIndex") ?: 0

    private val _state = MutableStateFlow(UiState(
        breedId = breedId,
        images = images,
        currentIndex = startIndex,
        loading = true,
        error = null
        )
    )
    val state = _state.asStateFlow()
    private fun setState(reducer : UiState.() -> UiState) = _state.getAndUpdate(reducer)

    private val events = MutableSharedFlow<UiEvent>()
    fun setEvent(event: UiEvent) = viewModelScope.launch {
        events.emit(event)
    }

    init {
        observeEvents()

        Log.d("PhotoViewerVM", "init: images.size=${images.size}, startIndex=$startIndex")
    }

    private fun observeEvents() = viewModelScope.launch{
        events.collect { event ->
            when(event){
                is UiEvent.LoadDetails -> loadImages()
                is UiEvent.SetPage -> setPage(event.index)
                else -> Unit
            }

        }
    }

    private fun loadImages() = viewModelScope.launch {
        try {
            repository.fetchAndCacheBreedImages(breedId)
            val entities = repository.getAllImagesForThisBreed(breedId).first()
            val urls = entities.map {
                it.url
            }
            Log.d("PhotoViewerVM", "Loaded ${urls.size} images for $breedId")
            setState { copy(images = urls, currentIndex = startIndex, loading = false) }
        }
        catch (t: Throwable) {
            Log.e("PhotoViewerVM", "Error loading images", t)
            setState { copy(error = t.toString(), currentIndex = startIndex, loading = false) }
        }
    }

    private fun setPage(index: Int) {
        Log.d("PhotoViewerVM", "setPage($index)")
        setState { copy(currentIndex = index) }
    }
}
