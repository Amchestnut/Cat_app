package com.example.cat_app.features.quiz.data.repository

import com.example.cat_app.features.quiz.data.local.QuizResultEntity
import com.example.cat_app.features.quiz.domain.Question
import kotlinx.coroutines.flow.Flow

interface QuizRepository {
    suspend fun generateQuiz(): List<Question>

    // functions for quiz result
    fun history(): Flow<List<QuizResultEntity>>
    fun bestScore(): Flow<Double>
    fun bestRanking(): Flow<Int?>
    suspend fun saveLocal(result: Double)
    suspend fun publish(result: Double): Int
}