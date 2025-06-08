package com.example.cat_app.features.profile.ui

interface ProfileScreenContract {

    data class UiState(
        val name: String = "",
        val nickname: String = "",
        val email: String = "",
        val loading: Boolean = true,
        val error: Throwable? = null
    )

    sealed class UiEvent {
        object LoadProfile : UiEvent()
        data class SaveProfile(
            val name: String,
            val nickname: String,
            val email: String
        ) : UiEvent()
    }

    sealed class SideEffect {
        object ProfileSaved : SideEffect()
    }
}