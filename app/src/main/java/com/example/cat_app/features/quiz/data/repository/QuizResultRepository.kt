package com.example.cat_app.features.quiz.data.repository

import com.example.cat_app.features.leaderboard.data.LeaderboardApiService
import com.example.cat_app.features.leaderboard.data.PostResultRequest
import com.example.cat_app.features.quiz.data.local.QuizResultDAO
import com.example.cat_app.features.quiz.data.local.QuizResultEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Named


interface QuizResultRepository {
    fun history(): Flow<List<QuizResultEntity>>
    fun bestScore(): Flow<Double>
    fun bestRanking(): Flow<Int?>
    suspend fun saveLocal(result: Double)
    suspend fun publish(result: Double): Int
}

