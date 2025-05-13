package com.example.cat_app.features.login_screen

interface LoginScreenContract {

    data class UiState(
        val name: String = "",
        val nickname: String = "",
        val email: String = "",
        val loading: Boolean = false,
        val error: String? = null
    )

    sealed class UiEvent {
        data class NameChanged(val name: String)           : UiEvent()
        data class NicknameChanged(val nickname: String)   : UiEvent()
        data class EmailChanged(val email: String)         : UiEvent()
        object Submit : UiEvent()
    }

    sealed class SideEffect {
        object NavigateToMain : SideEffect()
        data class ShowError(val message: String) : SideEffect()
    }

}