package com.example.cat_app.features.details_screen

import com.example.cat_app.features.allspecies.domain.Breed

interface SpeciesDetailsScreenContract {

    data class UiState(
        val loading: Boolean = false,
        val breed: Breed? = null,
        val error: Throwable? = null
    )

    sealed class UiEvent {
        // pokrece ucitavanje "Details" za dati ID  (ovo je MVI)
        object LoadDetails : UiEvent()     // kod profe je "data OBJECT", ali da  imamo parametre u klasi i da nama oni trebaju, imali bi "data CLASS"
    }

    sealed class SideEffect {
//        data object PasswordDeleted : SideEffect()
    }

}