package com.example.cat_app.features.leaderboard.data

import com.example.cat_app.features.quiz.data.local.QuizResultDTO
import javax.inject.Inject

class LeaderboardRepositoryImpl @Inject constructor(
    private val api: LeaderboardApiService
) : LeaderboardRepository {

    override suspend fun getLeaderboard(category: Int): List<QuizResultDTO> {
        return api.getLeaderboard(category)
    }

    override suspend fun submitResult(
        nickname: String,
        result: Double,
        category: Int
    ): PostResultResponse = api.postResult(
        PostResultRequest(nickname = nickname, result = result, category = category)
    )

}