package com.example.cat_app.photo_viewer


interface PhotoViewerContract {
    data class UiState(
        val images: List<String> = emptyList(),
        val currentIndex: Int = 0
    )

    sealed class UiEvent {
        data class SetPage(val index: Int) : UiEvent()
    }

    // nema side effektova
}