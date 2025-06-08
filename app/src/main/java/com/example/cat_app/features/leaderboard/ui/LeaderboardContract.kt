package com.example.cat_app.features.leaderboard.ui

interface LeaderboardContract {

    data class UiState(
        val loading: Boolean = false,
        val items: List<LeaderboardItem> = emptyList(),
        val errorMessage: String? = null
    )

    sealed class UiEvent {
        object LoadLeaderboard : UiEvent()
        object Refresh : UiEvent()
    }

    sealed class SideEffect {
        data class ShowError(val message: String) : SideEffect()
    }

    data class LeaderboardItem(
        val rank: Int,
        val nickname: String,
        val result: Double,
        val plays: Int
    )
}