package com.example.cat_app.features.profile.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cat_app.core.datastore.UserPreferencesRepository
import com.example.cat_app.features.profile.ui.ProfileScreenContract.*
import com.example.cat_app.features.quiz.data.repository.QuizRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userPrefs: UserPreferencesRepository,
    private val quizRepo: QuizRepository
) : ViewModel() {

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    private val events = MutableSharedFlow<UiEvent>()
    fun setEvent(e : UiEvent){
        viewModelScope.launch {
            events.emit(e)
        }
    }

    private val _effect = Channel<SideEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        observeEvents()
    }

    private fun observeEvents() {
        viewModelScope.launch {
            events.collect { event ->
                when(event){
                    is UiEvent.LoadProfile -> loadData()
                    else -> Unit
                }
            }
        }
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



    // ovo je za edit
    private fun saveProfile(evt: UiEvent.SaveProfile) {
        viewModelScope.launch {
            userPrefs.saveUserProfile(evt.name, evt.nickname, evt.email)
            _effect.send(SideEffect.ProfileSaved)
        }
    }
}