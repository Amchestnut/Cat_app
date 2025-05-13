package com.example.cat_app.features.splash_screen


interface SplashScreenContract {
    data class UiState(
        val loading: Boolean = false,
        val error: Throwable? = null
    )

    sealed class UiEvent {
        // pokrece ucitavanje "Details" za dati ID  (ovo je MVI)
//        data class LoadDetails(val breedId: String) : UiEvent()     // kod profe je "data OBJECT", ali mi imamo parametre u klasi i nama oni trebaju, pa je zato "data CLASS"
    }

    sealed class SideEffect {
        object NavigateToOnLogin : SideEffect()
        object NavigateToMain : SideEffect()
    }
}