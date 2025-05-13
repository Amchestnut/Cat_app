package com.example.cat_app.features.splash_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cat_app.core.datastore.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import com.example.cat_app.features.splash_screen.SplashScreenContract.UiState
import com.example.cat_app.features.splash_screen.SplashScreenContract.SideEffect
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val prefs : UserPreferencesRepository
) : ViewModel(){

    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    private val _effect = Channel<SideEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            prefs.userAlreadyExistsFlow.collect { exists ->
                val nav = if (exists)
                    SideEffect.NavigateToMain
                else
                    SideEffect.NavigateToOnLogin

                _effect.send(nav)
            }
        }
    }


}