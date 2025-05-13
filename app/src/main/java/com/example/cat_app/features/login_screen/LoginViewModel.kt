package com.example.cat_app.features.login_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cat_app.core.datastore.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.example.cat_app.features.login_screen.LoginScreenContract.UiState
import com.example.cat_app.features.login_screen.LoginScreenContract.UiEvent
import com.example.cat_app.features.login_screen.LoginScreenContract.SideEffect
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val prefs : UserPreferencesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()
    private fun setState(reducer : UiState.() -> UiState) = _state.getAndUpdate(reducer)

    private val _effect = Channel<SideEffect>()
    val effect = _effect.receiveAsFlow()
    private fun setEffect(some_effect : SideEffect) = viewModelScope.launch {
        _effect.send(some_effect)
    }


    fun setEvent(event : UiEvent) = viewModelScope.launch {
        when(event) {
            is UiEvent.NameChanged ->
                setState { copy(name = event.name) }

            is UiEvent.NicknameChanged ->
                setState { copy(nickname = event.nickname) }

            is UiEvent.EmailChanged ->
                setState { copy(email = event.email) }

            UiEvent.Submit -> {
                val current = _state.value
                if (current.name.isBlank() || current.nickname.isBlank() || current.email.isBlank()){
                    setEffect(SideEffect.ShowError("All fields are required"))
                }
                else{
                    setState {
                        copy(loading = true, error = null)
                    }
                    try{
                        prefs.saveUserProfile(current.name, current.nickname, current.email)
                        setEffect(SideEffect.NavigateToMain)
                    }
                    catch (t: Throwable){
                        setState {
                            copy(loading = false, error = t.message)
                        }
                        setEffect(SideEffect.ShowError(t.message ?: "Unknown error"))
                    }
                }
            }
        }
    }


}