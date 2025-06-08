package com.example.cat_app.features.profile.ui

import com.example.cat_app.features.quiz.data.local.QuizResultEntity

interface ProfileScreenContract {

    data class UiState(
        val name: String = "",
        val nickname: String = "",
        val email: String = "",
        val loading: Boolean = true,
        val error: Throwable? = null,

        // novo, za leaderboard
        val history: List<QuizResultEntity> = emptyList(),
        val bestScore: Double = 0.0,
        val bestRanking: Int? = null,
    )

    sealed class UiEvent {
        object LoadProfile : UiEvent()
        data class SaveProfile(
            val name: String,
            val nickname: String,
            val email: String
        ) : UiEvent()
    }

    sealed class SideEffect {
        object ProfileSaved : SideEffect()
    }
}