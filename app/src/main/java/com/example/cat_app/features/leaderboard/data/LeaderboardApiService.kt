package com.example.cat_app.features.leaderboard.data

import com.example.cat_app.features.quiz.data.local.QuizResultDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface LeaderboardApiService {
    @GET("leaderboard")
    suspend fun getLeaderboard(
        @Query("category") category: Int
    ): List<QuizResultDTO>

    @POST("leaderboard")
    suspend fun postResult(
        @Body request: PostResultRequest
    ): PostResultResponse
}