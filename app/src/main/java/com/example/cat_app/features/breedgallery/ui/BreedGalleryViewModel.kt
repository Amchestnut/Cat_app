package com.example.cat_app.features.breedgallery.ui

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import java.lang.IllegalArgumentException
import javax.inject.Inject
import com.example.cat_app.features.breedgallery.ui.BreedGalleryContract.UiState
import com.example.cat_app.features.breedgallery.ui.BreedGalleryContract.UiEvent
import com.example.cat_app.features.breedgallery.ui.BreedGalleryContract.SideEffect
import com.example.cat_app.features.breedgallery.data.repository.BreedImagesRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch


@HiltViewModel
class BreedGalleryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: BreedImagesRepository
) : ViewModel(){

    private val breedId: String = savedStateHandle.get<String>("breedId") ?: throw IllegalArgumentException("breedId is required")

    private val _state = MutableStateFlow(UiState(breedId = breedId))
    val state = _state.asStateFlow()
    private fun setState(reducer: UiState.() -> UiState) = _state.getAndUpdate(reducer)

    private val events = MutableSharedFlow<UiEvent>()
    fun setEvent(event: UiEvent) = viewModelScope.launch {
        events.emit(event)
    }

    private val _effect = Channel<SideEffect>()
    val effect = _effect.receiveAsFlow()


    init{
        observeEvents()
    }

    private fun observeEvents() = viewModelScope.launch {
        events.collect { event ->
            when (event) {
                is UiEvent.LoadImages -> loadImages(event.breedId)
            }
        }
    }

    private fun loadImages(id: String) {
        viewModelScope.launch {
            setState { copy(loading = false, error = null) }

            try {
                repository.fetchAndCacheBreedImages(id)
                val images = repository.getAllImagesForThisBreed(id).first()      // first() suspenduje dok Flow ne emituje PRVU vrednost (sto ce biti trenutni sadrzaj baze), a images dobije listu svih slika koje sam ranije sacuvao u ROOM-u
                setState { copy(loading = false, images = images) }

                Log.d("BreedGalleryVM", "Loaded ${images.size} images: ${images.map { it.url }}")
            }
            catch (t : Throwable){
                Log.e("BreedGalleryVM", "Error loading images", t)
                setState { copy(loading = false, error = t) }
                _effect.send(SideEffect.ShowErrorMessage(t.localizedMessage ?: "Unknown error"))
            }
        }
    }

}