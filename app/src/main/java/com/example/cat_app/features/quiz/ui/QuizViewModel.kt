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
import com.example.cat_app.features.quiz.domain.TOTAL_TIME_MS
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
import kotlin.math.roundToInt


@HiltViewModel
class QuizViewModel @Inject constructor(
    private val quizRepository: QuizRepository,
): ViewModel() {
    val TAG = "QuizViewModel"       // za loggere

    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()
    private fun setState(reducer: UiState.() -> UiState) = _state.getAndUpdate(reducer)

    private val events = MutableSharedFlow<UiEvent>()
    fun setEvent(e: UiEvent) = viewModelScope.launch {
        events.emit(e)
    }

    private val _effect = Channel<SideEffect>()
    val effect = _effect.receiveAsFlow()

    // job za timer, cuvam ga da bi mogao da cancel-ujem ako zavrsimo ili ako kliknemo cancel
    private var timerJob: Job? = null


    init {
        Log.d(TAG, "Initializing ViewModel, loading quiz…")
        observeEvents()
    }

    private fun observeEvents() = viewModelScope.launch{
        events.collect{ event ->
            when(event) {
                is UiEvent.LoadQuiz -> load()
                is UiEvent.AnswerChosen -> answer(event.answer)
                is UiEvent.Tick -> tick()
                is UiEvent.CancelPressed -> _effect.send(
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
            val questions = quizRepository.generateQuiz()
            Log.d(TAG, "Loaded ${questions.size} questions: $questions")

            setState { copy(questions = questions) }
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

        val currentQuestion = s.questions[s.currentIdx]
        val selected = value.toString()
        val isCorrect = selected == currentQuestion.correctChoice

        setState {
            // 1) ažuriraj BTO
            val updatedBto = correctAnswers + if (isCorrect) 1 else 0

            // 2) preostalo vreme u sekundama
            val pvt = remainingMillis / 1000.0
            // 3) maksimalno vreme
            val mvt = TOTAL_TIME_MS / 1000.0

            // 4) UBP formula i limit
            val ukupanBrojPoena = updatedBto * 2.5 * (1 + (pvt + 120) / mvt)
            val max100 = ukupanBrojPoena.coerceAtMost(100.0)
            val totalScore  = (max100 * 100).roundToInt() / 100.0

            copy(
                correctAnswers = updatedBto,
                currentIdx     = currentIdx + 1,
                finished       = (currentIdx + 1) >= questions.size,
                totalScore     = totalScore
            )
        }

        // ako smo zavrsili, promenili smo bili finished FALSE -> TRUE, i okinucemo ovaj finish()
        if (_state.value.finished) {
            Log.d(TAG, "All questions answered, finishing quiz…")
            finish()   // mogli smo i da wrappujemo u event
        }
    }



    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1_000)
                setEvent(UiEvent.Tick)      // smanji tajmer za 1s
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
        timerJob?.cancel()
        val s = state.value

        // ponovo izračunamo za log i za slanje
        val bto = s.correctAnswers
        val pvt = s.remainingMillis / 1000.0
        val mvt = TOTAL_TIME_MS   / 1000.0

        val ukupanBrojPoena = bto * 2.5 * (1 + (pvt + 120) / mvt)
        val max100 = ukupanBrojPoena.coerceAtMost(100.0)
        val totalScore  = (max100 * 100).roundToInt() / 100.0

        Log.d(TAG, "finish(): BTO=$bto, PVT=$pvt, ukupanBrojPoena=$ukupanBrojPoena, totalScore=$totalScore")

        viewModelScope.launch {
            quizRepository.saveLocal(totalScore)
        }
        _effect.trySend(SideEffect.NavigateToResult)
    }

    private fun hardCancel() {
        timerJob?.cancel()
        setState {      // reset
            UiState()
        }
    }


    private fun share() = viewModelScope.launch {
        Log.d(TAG, "share(): SharePressed event received, starting publish…")
        setState { copy(posting = true) }

        kotlin.runCatching {
            val total = state.value.totalScore           // I send my total score
            val ranking = quizRepository.publish(total)  // I receive the my new ranking on leaderboard

            Log.d(TAG, "share(): publishing totalScore=$total")
            Log.d(TAG, "share(): publish returned ranking=$ranking")
        }.onSuccess { ranking ->
            Log.d(TAG, "share(): Score successfully shared! Server ranking=$ranking")
            _effect.trySend(SideEffect.ScoreShared)
        }.onFailure { err ->
            Log.e(TAG, "share(): Error publishing score", err)
            setState { copy(error = err) }
        }.also {
            Log.d(TAG, "share(): setting posting=false")
            setState { copy(posting = false) }
        }
    }



}
