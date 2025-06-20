package com.example.cat_app.features.quiz.ui

import com.example.cat_app.features.quiz.domain.TOTAL_TIME_MS
import com.example.cat_app.features.quiz.domain.Question

interface QuizScreenContract {
    data class UiState(
        val questions: List<Question> = emptyList(),
        val currentIdx: Int = 0,
//        val answers: List<Int?> = List(20) { null },
        val correctAnswers: Int = 0,
        val remainingMillis: Long = TOTAL_TIME_MS,
        val finished: Boolean = false,
        val posting: Boolean = false,
        val error: Throwable? = null,
        val totalScore: Double = 0.0
    )

    sealed interface UiEvent {
        data object LoadQuiz: UiEvent
        data class AnswerChosen(val answer: Any): UiEvent
        data object Tick : UiEvent          // 1-sec tick tack
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
