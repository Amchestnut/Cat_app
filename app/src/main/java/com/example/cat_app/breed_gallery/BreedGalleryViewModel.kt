package com.example.cat_app.breed_gallery

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import java.lang.IllegalArgumentException
import javax.inject.Inject
import com.example.cat_app.breed_gallery.BreedGalleryContract.UiState
import com.example.cat_app.breed_gallery.BreedGalleryContract.UiEvent
import com.example.cat_app.breed_gallery.BreedGalleryContract.SideEffect
import com.example.cat_app.repository.BreedImagesRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.asStateFlow
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

    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()
    private fun setState(reducer: UiState.() -> UiState) = _state.getAndUpdate(reducer)

    private val _effect = Channel<SideEffect>()
    val effect = _effect.receiveAsFlow()


    init{
        setEvent(UiEvent.LoadImages(breedId))
    }

    fun setEvent(event: UiEvent) {
        when (event) {
            is UiEvent.LoadImages -> loadImages(event.breedId)
        }
    }

    private fun loadImages(id: String) {
        viewModelScope.launch {
            setState { copy(loading = false, error = null) }

            try {
                repository.fetchBreedImages(id)
                val images = repository.observeBreedImages(id).first()
                setState { copy(loading = false, images = images) }
            }
            catch (t : Throwable){
                setState { copy(loading = false, error = t) }
                _effect.send(SideEffect.ShowErrorMessage(t.localizedMessage ?: "Unknown error"))
            }
        }
    }

}