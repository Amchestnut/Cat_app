package com.example.cat_app.breed_gallery

interface BreedGalleryContract {

    data class UiState(
        val loading: Boolean = false,
        val images: List<BreedImage> = emptyList(),
        val error: Throwable? = null
    )

    sealed class UiEvent {
        data class LoadImages(val breedId: String) : UiEvent()
    }

    sealed class SideEffect {
        data class ShowErrorMessage(val message: String) : SideEffect()
    }
}