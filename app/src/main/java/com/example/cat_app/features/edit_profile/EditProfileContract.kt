package com.example.cat_app.features.edit_profile

interface EditProfileContract {

    data class UiState(
        val name: String = "",
        val nickname: String = "",
        val email: String = "",
        val loading: Boolean = true,
        val error: Throwable? = null
    )

    sealed class UiEvent {
        object LoadData : UiEvent()
        data class NameChanged(val name: String) : UiEvent()
        data class NicknameChanged(val nickname: String) : UiEvent()
        data class EmailChanged(val email: String) : UiEvent()
        object SaveProfile : UiEvent()
    }

    sealed class SideEffect {
        object ProfileSaved : SideEffect()
    }

}