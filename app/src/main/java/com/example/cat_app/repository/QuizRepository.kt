package com.example.cat_app.repository

import com.example.cat_app.quiz_package.questions.Question

interface QuizRepository {
    suspend fun generateQuiz(): List<Question>
    suspend fun postScore(score: Int)
}