package com.example.cat_app.photo_viewer


interface PhotoViewerContract {
    data class UiState(
        val images: List<String> = emptyList(),
        val currentIndex: Int = 0,
        val loading: Boolean = false,
        val error: String? = null
    )

    sealed class UiEvent {
        data class SetPage(val index: Int) : UiEvent()
    }

    // nema side effektova
}