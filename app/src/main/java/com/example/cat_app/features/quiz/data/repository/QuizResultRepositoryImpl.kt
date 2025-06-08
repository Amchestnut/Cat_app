package com.example.cat_app.features.quiz.data.repository

import android.util.Log
import com.example.cat_app.core.database.AppDatabase
import com.example.cat_app.core.datastore.UserPreferencesRepository
import com.example.cat_app.features.leaderboard.data.LeaderboardApiService
import com.example.cat_app.features.leaderboard.data.PostResultRequest
import com.example.cat_app.features.quiz.data.local.QuizResultDAO
import com.example.cat_app.features.quiz.data.local.QuizResultEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class QuizResultRepositoryImpl @Inject constructor(
    private val api: LeaderboardApiService,       // retrofit interfejs za POST i GET
    private val db: AppDatabase,
    private val userPrefs: UserPreferencesRepository,
) : QuizResultRepository{
    // istorija svih kvizova
    override fun history(): Flow<List<QuizResultEntity>> = db.quizResultDao().getAllResults()

    // TODO: najbolji lokalni skor
    override fun bestScore(): Flow<Double> =
        db.quizResultDao().getBestResult().map { it?.result ?: 0.0 }

    // najbolja globalna pozicija (ako je bar jedan objavljen)
    override fun bestRanking(): Flow<Int?> = db.quizResultDao().getBestRanking()

    // samo lokalno čuvanje (pre nego što se objavi)
    override suspend fun saveLocal(result: Double) {
        Log.d("QuizResultRepo", "saveLocal: inserting result=$result")
        db.quizResultDao().insert(
            QuizResultEntity(
            result = result,
            timestamp = System.currentTimeMillis()
            )
        )
        Log.d("QuizResultRepo", "saveLocal: insert complete")
    }

    // objavicu na API i upisacu i u lokalnu bazu sa rankingom!!
    override suspend fun publish(result: Double): Int {
        val nickname = userPrefs.nicknameFlow.first()
        val resp = api.postResult(PostResultRequest(nickname, result, 1))
        val ranking = resp.ranking


        db.quizResultDao().insert(
            QuizResultEntity(
            result = result,
            timestamp = System.currentTimeMillis(),
            published = true,
            ranking = ranking
        )
        )
        return ranking
    }
}