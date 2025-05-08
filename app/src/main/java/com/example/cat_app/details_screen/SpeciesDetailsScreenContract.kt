package com.example.cat_app.details_screen

import com.example.cat_app.domain.Breed

interface SpeciesDetailsScreenContract {

    data class UiState(
        val loading: Boolean = false,
        val breed: Breed? = null,
        val error: Throwable? = null
    )

    sealed class UiEvent {
        // pokrece ucitavanje "Details" za dati ID  (ovo je MVI)
        data class LoadDetails(val breedId: String) : UiEvent()     // kod profe je "data OBJECT", ali mi imamo parametre u klasi i nama oni trebaju, pa je zato "data CLASS"
    }

    sealed class SideEffect {
//        data object PasswordDeleted : SideEffect()
    }

}