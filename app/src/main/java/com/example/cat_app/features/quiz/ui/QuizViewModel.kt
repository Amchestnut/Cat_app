package com.example.cat_app.features.quiz.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import com.example.cat_app.features.quiz.ui.QuizScreenContract.UiState
import com.example.cat_app.features.quiz.ui.QuizScreenContract.UiEvent
import com.example.cat_app.features.quiz.ui.QuizScreenContract.SideEffect
import com.example.cat_app.features.quiz.data.repository.QuizRepository
import com.example.cat_app.features.quiz.data.repository.QuizResultRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


@HiltViewModel
class QuizViewModel @Inject constructor(
    private val repo: QuizRepository,
    private val quizResultRepository: QuizResultRepository
): ViewModel() {
    val TAG = "QuizViewModel"       // za loggere

    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()
    private fun setState(reducer: UiState.() -> UiState) = _state.getAndUpdate(reducer)

    private val events = MutableSharedFlow<UiEvent>()
    fun setEvent(e: UiEvent) = viewModelScope.launch {
        events.emit(e)
    }
//    fun setEvent(e: UiEvent) = viewModelScope.launch {
//        when (e) {
//            UiEvent.LoadQuiz -> load()
//            is UiEvent.AnswerChosen -> answer(e.answer)
//            UiEvent.Tick -> tick()
//            UiEvent.CancelPressed -> _effect.send(
//                SideEffect.ShowCancelDialog { hardCancel() }
//            )
//            UiEvent.TimeUp -> finish()
//            UiEvent.SharePressed -> share()
//        }
//    }

    private val _effect = Channel<SideEffect>()
    val effect = _effect.receiveAsFlow()

    private var timerJob: Job? = null


    init {
        Log.d(TAG, "Initializing ViewModel, loading quiz…")
        observeEvents()
//        setEvent(UiEvent.LoadQuiz)    posto prvo pozivam "Intro screen", ne treba mi ovde load quiz, vec se poziva load quiz kada predjemo na QUESTIONS screen
    }

    private fun observeEvents() = viewModelScope.launch{
        events.collect{ event ->
            when(event) {
                is UiEvent.LoadQuiz -> load()
                is UiEvent.AnswerChosen -> answer(event.answer)
                is UiEvent.Tick -> tick()
                is  UiEvent.CancelPressed -> _effect.send(
                    SideEffect.ShowCancelDialog { hardCancel() }
                )
                is UiEvent.TimeUp -> finish()
                is UiEvent.SharePressed -> share()
            }
        }
    }


    private suspend fun load() {
        Log.d(TAG, "Calling repo.generateQuiz()")
        try {
            val questions = repo.generateQuiz()
            Log.d(TAG, "Loaded ${questions.size} questions: $questions")
            setState { copy(questions = questions) }
//            setState { copy(questions = questions, answers = List(questions.size) { null }, currentIdx = 0, finished = false, remainingMillis = TOTAL_TIME_MS) }
            startTimer()
        }
        catch (err: Throwable) {
            Log.e(TAG, "Error loading quiz", err)
            setState { copy(error = err) }
        }
    }

    private fun answer(value: Any) {
        val s = _state.value
        if (s.finished){
            Log.d(TAG, "AnswerChosen called but already finished")
            return
        }

        val curr_question = s.questions.getOrNull(s.currentIdx)
        Log.d(TAG, "Answering question #${s.currentIdx + 1}: $curr_question")
        Log.d(TAG, "User chose: $value")

        val pts = s.questions[s.currentIdx].score(value.toString())
        setState {
            copy(
                answers = answers.toMutableList().also { it[currentIdx] = pts },
                currentIdx = currentIdx + 1,
                finished = currentIdx + 1 == questions.size
            )
        }

        // samo za logovanje
        val newState = _state.value
        Log.d(TAG, "New state → currentIdx=${newState.currentIdx}, answers=${newState.answers}, finished=${newState.finished}")


        if (_state.value.finished) {
            Log.d(TAG, "All questions answered, finishing quiz…")
            finish()
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1_000)
                setEvent(UiEvent.Tick)
            }
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
        Log.d(TAG, "finish(): local save done, now navigating to result")
        viewModelScope.launch {
            quizResultRepository.saveLocal(totalScore().toDouble())
        }

        Log.d(TAG, "finish() called, sending NavigateToResult")
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
