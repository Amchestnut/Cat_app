package com.example.cat_app.features.leaderboard.data

import com.example.cat_app.features.quiz.data.local.QuizResultDTO

interface LeaderboardRepository {
    suspend fun getLeaderboard(category: Int): List<QuizResultDTO>

}