package com.example.cat_app.quiz_package

import com.example.cat_app.quiz_package.questions.Question
import com.example.cat_app.quiz_package.questions.QuestionFactory

interface QuizScreenContract {
    data class UiState(
        val questions: List<Question> = emptyList(),
        val currentIdx: Int = 0,
        val answers: List<Int?> = List(20) { null },
        val remainingMillis: Long = TOTAL_TIME_MS,
        val finished: Boolean = false,
        val posting: Boolean = false,
        val error: Throwable? = null,
    )

    sealed interface UiEvent {
        data object LoadQuiz: UiEvent
        data class AnswerChosen(val answer: Any): UiEvent
        data object Tick : UiEvent          // 1-sec heartbeat
        data object CancelPressed: UiEvent
        data object TimeUp: UiEvent
        data object SharePressed: UiEvent
    }

    sealed interface SideEffect {
        data class ShowCancelDialog(val onConfirm: () -> Unit): SideEffect
        data class ShowError(val msg: String): SideEffect
        data object NavigateToResult: SideEffect
        data object ScoreShared: SideEffect
    }
}
