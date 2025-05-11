package com.example.cat_app.quiz_package

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import com.example.cat_app.quiz_package.QuizScreenContract.UiState
import com.example.cat_app.quiz_package.QuizScreenContract.UiEvent
import com.example.cat_app.quiz_package.QuizScreenContract.SideEffect
import com.example.cat_app.repository.QuizRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch


@HiltViewModel
class QuizViewModel @Inject constructor(
    private val repo: QuizRepository
): ViewModel() {

    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()
    private fun setState(reducer: UiState.() -> UiState) = _state.getAndUpdate(reducer)


    private val events = MutableSharedFlow<UiEvent>()
    fun setEvent(e: UiEvent) = viewModelScope.launch {
        when (e) {
            UiEvent.LoadQuiz -> load()
            is UiEvent.AnswerChosen -> answer(e.answer)
            UiEvent.Tick -> tick()
            UiEvent.CancelPressed -> _effect.send(
                SideEffect.ShowCancelDialog { hardCancel() }
            )
            UiEvent.TimeUp -> finish()
            UiEvent.SharePressed -> share()
        }
    }

    private val _effect = Channel<SideEffect>()
    val effect = _effect.receiveAsFlow()

    private var timerJob: Job? = null       // jos necemo ovo

    // START, load sva pitanja, pripremi ih za pravi kviz
    // TODO: da li ovo odmah ili kad kliknem start?
    init {
        setEvent(UiEvent.LoadQuiz)
    }

    // ovo moze i sa runCatching, mada je on isti kao try and catch
    private suspend fun load() {
        try {
            val questions = repo.generateQuiz()
            setState { copy(questions = questions) }
            startTimer()
        }
        catch (err: Throwable) {
            setState { copy(error = err) }
        }
    }

    private fun answer(value: Any) {
        val s = _state.value
        if (s.finished) return

        val pts = s.questions[s.currentIdx].score(value.toString())
        setState {
            copy(
                answers = answers.toMutableList().also { it[currentIdx] = pts },
                currentIdx = currentIdx + 1,
                finished = currentIdx + 1 == questions.size
            )
        }
        if (_state.value.finished) finish()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
//            while (isActive) {
//                delay(1_000)
//                setEvent(UiEvent.Tick)
//            }
            // TODO, ne razumem zasto mi ne radi ovaj isActive
        }
    }

    private fun tick() {
        setState {
            val left = remainingMillis - 1_000
            copy(remainingMillis = left)
        }
        if (_state.value.remainingMillis <= 0) {
            setEvent(UiEvent.TimeUp)
        }
    }

    private fun finish() {
        timerJob?.cancel()
        _effect.trySend(SideEffect.NavigateToResult)
    }

    private fun hardCancel() {
        timerJob?.cancel()
        setState {      // reset
            UiState()
        }
    }

    private fun share() = viewModelScope.launch {
        setState { copy(posting = true) }
        kotlin.runCatching {
            repo.postScore(totalScore())
        }.onSuccess {
            _effect.send(SideEffect.ScoreShared)
        }.onFailure { err ->
            setState { copy(error = err) }
        }.also {
            setState { copy(posting = false) }
        }
    }

    fun totalScore() = _state.value.answers.filterNotNull().sum()
}
