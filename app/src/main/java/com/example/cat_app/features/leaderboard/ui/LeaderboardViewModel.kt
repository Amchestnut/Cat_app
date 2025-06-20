package com.example.cat_app.features.leaderboard.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cat_app.features.leaderboard.data.LeaderboardRepository
import com.example.cat_app.features.leaderboard.ui.LeaderboardContract.*
import com.example.cat_app.features.quiz.ui.QuizScreenContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val repository: LeaderboardRepository
) : ViewModel() {

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()
    private fun setState(reducer: UiState.() -> UiState) = _state.getAndUpdate(reducer)


    private val events = MutableSharedFlow<UiEvent>()
    fun setEvent(e: UiEvent) {
        viewModelScope.launch {
            events.emit(e)
        }
    }

    private val _effect = MutableSharedFlow<SideEffect>()
    val effect: SharedFlow<SideEffect> = _effect.asSharedFlow()

    init {
        observeEvents()
    }

    private fun observeEvents() {
        viewModelScope.launch {
            events.collect { event ->
                when (event) {
                    UiEvent.LoadLeaderboard -> load()
                    UiEvent.Refresh -> load()
                }
            }
        }
    }

    private fun load() {
        viewModelScope.launch {
            setState {
                copy(loading = true, errorMessage = null)
            }
            try {
                val raw = repository.getLeaderboard(1)      // u API documentation je receno da treba da saljem broj 1

                // calculate plays per nickname
                val playsMap = raw.groupingBy {
                    it.nickname
                }.eachCount()

                // build UI items
                val items = raw.mapIndexed { idx, dto ->
                    LeaderboardItem(
                        rank     = idx + 1,
                        nickname = dto.nickname,
                        result   = dto.result,
                        plays    = playsMap[dto.nickname] ?: 1
                    )
                }

                // setovacu sve iteme koje sam ucitao, da bi ih prikazao na UI
                setState {
                    copy(loading = false, items = items)
                }

            } catch (t: Throwable) {
                setState {
                    copy(loading = false, errorMessage = t.message)
                }
            }
        }
    }

}
