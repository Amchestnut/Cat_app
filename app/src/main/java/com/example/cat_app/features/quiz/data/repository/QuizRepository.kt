package com.example.cat_app.features.quiz.data.repository

import com.example.cat_app.features.quiz.domain.Question

interface QuizRepository {
    suspend fun generateQuiz(): List<Question>
    suspend fun postScore(score: Int)
}