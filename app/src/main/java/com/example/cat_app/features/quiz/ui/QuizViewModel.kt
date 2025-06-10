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
                is  UiEvent.CancelPressed -> _effect.send(
                    SideEffect.ShowCancelDialog { hardCancel() }
                )
                is UiEvent.TimeUp -> finish()
                is UiEvent.SharePressed -> share()
            }
        }
    }


    /**
     load():
     - suspend funkcija koja poziva repo.generateQuiz()
     - u slučaju uspeha postavlja lista pitanja u state
     - pokreće timer
     - u slučaju greške hvata izuzetak i beleži u state.error
     */
    private suspend fun load() {
        Log.d(TAG, "Calling repo.generateQuiz()")
        try {
            val questions = repo.generateQuiz()
            Log.d(TAG, "Loaded ${questions.size} questions: $questions")

            setState { copy(questions = questions) }
            startTimer()
        }
        catch (err: Throwable) {
            Log.e(TAG, "Error loading quiz", err)
            setState { copy(error = err) }
        }
    }

    /**
     * answer(value):
     * - ne radi ništa ako je quiz već završen
     * - izračuna broj poena za trenutni odgovor
     * - napuni answers listu, inkrementuje currentIdx
     * - ako smo došli do kraja, poziva finish()
     *
     * Ovde mogu da dodam  UI efekat (pucanje konfete) slanjem SideEffect.
     */
    private fun answer(value: Any) {
        val s = _state.value
        if (s.finished){
            Log.d(TAG, "AnswerChosen called but already finished")
            return
        }

        val currentQuestion = s.questions.getOrNull(s.currentIdx)
        Log.d(TAG, "Answering question #${s.currentIdx + 1}: $currentQuestion")
        Log.d(TAG, "User chose: $value")

        val points = s.questions[s.currentIdx].score(value.toString())
        setState {
            val newAnswers = answers.toMutableList().also { it[currentIdx] = points }
            // uradicemo ovo direktno ovde, a ne pomocu neke pomocne funkcije
            val newTotal   = newAnswers.filterNotNull().sum()
            copy(
                answers    = newAnswers,
                currentIdx = currentIdx + 1,                    // inkrementujemo pitanje na sledece
                finished = currentIdx + 1 == questions.size,     // prost boolean, jel smo dosli do kraja?
                totalScore = newTotal,
            )
        }


        // samo za LOGCAT
        val newState = _state.value
        Log.d(TAG, "New state → currentIdx=${newState.currentIdx}, answers=${newState.answers}, finished=${newState.finished}")

        // ako smo zavrsili, promenili smo bili finished FALSE -> TRUE, i okinucemo ovaj finish()
        if (_state.value.finished) {
            Log.d(TAG, "All questions answered, finishing quiz…")
            finish()   // mogli smo i da wrappujemo u event
        }
    }


    /**
     * startTimer():
     * - cancel-uje postojeći timerJob ako postoji
     * - pokreće novi coroutine koji svakih 1s emituje UiEvent.Tick
     */
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

    /**
     * finish():
     * - čuva rezultat lokalno (quizResultRepository.saveLocal)
     * - cancel-uje timerJob
     * - šalje SideEffect.NavigateToResult da UI zatvori screen
     *
     * Savrseno vreme za ubacivanje neke animacije
     */
    private fun finish() {
        Log.d(TAG, "finish(): local save done, now navigating to result")
        viewModelScope.launch {
            val total = state.value.totalScore
            quizResultRepository.saveLocal(total.toDouble())
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


    /**
     * share():
     * - postavlja posting = true u state (da UI može da prikaže loader)
     * - poziva repo.postScore(totalScore())
     * - pri uspehu emituje SideEffect.ScoreShared
     * - pri grešci upisuje err u state.error
     * - na kraju resetuje posting = false
     *
     * Ovde bi bilo kul da dodam neku animaciju
     */
    private fun share() = viewModelScope.launch {
        setState { copy(posting = true) }
        kotlin.runCatching {
            val total = state.value.totalScore
            repo.postScore(total)
        }.onSuccess {
            Log.d(TAG, "Score successfully shared to server!")
            /*  ViewModel po pravilu MVI ne sme direktno da prikazuje snackbar, toast ili dialog, nego je ovo SIDE EFFECT, pa mozemo ovo da uhvatimo u UI i prikazemo
                Takodje, bolje da koristim trySend nego send, jer viewmodel ce ostati blokiran ako UI trenutno ne slusa.  */

            _effect.trySend(SideEffect.ScoreShared)

        }.onFailure { err ->
            setState { copy(error = err) }
        }.also {
            setState { copy(posting = false) }
        }
    }

}
