package com.example.cat_app.features.leaderboard.data

import com.example.cat_app.features.quiz.data.local.QuizResultDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface LeaderboardApi {
    @GET("leaderboard")
    suspend fun getLeaderboard(
        @Query("category") category: Int
    ): List<QuizResultDTO>

    // ovo nam zapravo treba u QuizRepository, a ne u LeaderboardRepository, tako da smo ga koristili tamo!
    @POST("leaderboard")
    suspend fun postResult(
        @Body request: PostResultRequest    /// u telu funkcije saljem IME, SKOR, i kategoriju 1
    ): PostResultResponse   /// kao rezultat API-ja (response), ja dobijam 2 stvari, QuizResult (profesorov api mi vrati 4 stvari koje meni trebaju da napravim quizresult) i vrati mi RANKING.   Oba ova parametra cuvam u POST RESULT RESPONSE
}