package com.example.cat_app.features.edit_profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cat_app.core.datastore.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userPrefs: UserPreferencesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(EditProfileContract.UiState())
    val state: StateFlow<EditProfileContract.UiState> = _state

    private val events = MutableSharedFlow<EditProfileContract.UiEvent>()
    fun setEvent(e: EditProfileContract.UiEvent) {
        viewModelScope.launch { events.emit(e) }
    }

    private val _effect = Channel<EditProfileContract.SideEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        observeEvents()

    }

    private fun observeEvents() {
        viewModelScope.launch {
            events.collect { event ->
                when (event) {
                    is EditProfileContract.UiEvent.LoadData -> loadData()
                    is EditProfileContract.UiEvent.NameChanged ->
                        _state.update { it.copy(name = event.name) }
                    is EditProfileContract.UiEvent.NicknameChanged ->
                        _state.update { it.copy(nickname = event.nickname) }
                    is EditProfileContract.UiEvent.EmailChanged ->
                        _state.update { it.copy(email = event.email) }
                    is EditProfileContract.UiEvent.SaveProfile -> saveProfile()
                }
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            try {
                val name    = userPrefs.nameFlow.first()
                val nickname = userPrefs.nicknameFlow.first()
                val email    = userPrefs.emailFlow.first()
                _state.value = EditProfileContract.UiState(
                    name = name,
                    nickname = nickname,
                    email = email,
                    loading = false
                )
            } catch (t: Throwable) {
                _state.update { it.copy(loading = false, error = t) }
            }
        }
    }

    private fun saveProfile() {
        viewModelScope.launch {
            val s = state.value
            userPrefs.saveUserProfile(s.name, s.nickname, s.email)
            _effect.send(EditProfileContract.SideEffect.ProfileSaved)
        }
    }
}