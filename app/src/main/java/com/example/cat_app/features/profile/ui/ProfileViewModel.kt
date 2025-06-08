package com.example.cat_app.features.profile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cat_app.core.datastore.UserPreferencesRepository
import com.example.cat_app.features.profile.ui.ProfileScreenContract.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userPrefs: UserPreferencesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    private val _effect = Channel<SideEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        // load stored profile into UiState
        viewModelScope.launch {
            combine(
                userPrefs.nameFlow,
                userPrefs.nicknameFlow,
                userPrefs.emailFlow
            ) { name, nick, email ->
                UiState(name = name, nickname = nick, email = email, loading = false)
            }
                .catch { t -> _state.update { it.copy(loading = false, error = t) } }
                .collect { _state.value = it }
        }
    }

    fun setEvent(event: UiEvent) {
        when (event) {
            UiEvent.LoadProfile -> { /* already loaded in init */ }
            is UiEvent.SaveProfile -> saveProfile(event)
        }
    }

    private fun saveProfile(evt: UiEvent.SaveProfile) {
        viewModelScope.launch {
            userPrefs.saveUserProfile(evt.name, evt.nickname, evt.email)
            _effect.send(SideEffect.ProfileSaved)
        }
    }
}