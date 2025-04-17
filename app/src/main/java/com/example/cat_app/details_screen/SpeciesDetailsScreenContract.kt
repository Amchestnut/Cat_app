package com.example.cat_app.details_screen

import com.example.cat_app.domain.Breed

interface SpeciesDetailsScreenContract {

    data class UiState(
        val loading: Boolean = false,
        val breed: Breed? = null,
        val error: Throwable? = null
    )

    sealed class UiEvent {
        // pokrece ucitavanje "Details" za dati ID
        data class LoadDetails(val speciesId: String) : UiEvent()
    }

    sealed class SideEffect {
//        data object PasswordDeleted : SideEffect()
    }

}