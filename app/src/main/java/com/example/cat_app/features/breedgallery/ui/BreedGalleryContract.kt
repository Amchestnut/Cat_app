package com.example.cat_app.features.breedgallery.ui

import com.example.cat_app.features.breedgallery.domain.BreedImage

interface BreedGalleryContract {

    data class UiState(
        val loading: Boolean = false,
        val images: List<BreedImage> = emptyList(),
        val error: Throwable? = null,
        val breedId: String = "",
    )

    sealed class UiEvent {
        data class LoadImages(val breedId: String) : UiEvent()
    }

    sealed class SideEffect {
        data class ShowErrorMessage(val message: String) : SideEffect()
    }
}