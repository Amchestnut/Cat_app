package com.example.cat_app.features.profile.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cat_app.core.datastore.UserPreferencesRepository
import com.example.cat_app.features.profile.ui.ProfileScreenContract.*
import com.example.cat_app.features.quiz.data.repository.QuizResultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userPrefs: UserPreferencesRepository,
    private val quizRepo: QuizResultRepository
) : ViewModel() {

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    private val _effect = Channel<SideEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            try {
                // jednom uzmemo snapshot vrednosti iz svih izvora
                val name      = userPrefs.nameFlow.first()
                val nickname  = userPrefs.nicknameFlow.first()
                val email     = userPrefs.emailFlow.first()

                val history   = quizRepo.history().first()
                val bestScore = quizRepo.bestScore().first()
                val bestRank  = quizRepo.bestRanking().first()

                Log.d("ProfileVM", "loadData(): fetched history size=${history.size}")

                _state.value = UiState(
                    name        = name,
                    nickname    = nickname,
                    email       = email,
                    history     = history,
                    bestScore   = bestScore,
                    bestRanking = bestRank,
                    loading     = false
                )
            } catch (t: Throwable) {
                _state.update { it.copy(loading = false, error = t) }
            }
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