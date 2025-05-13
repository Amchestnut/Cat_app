package com.example.cat_app.features.quiz.domain

data class QuizQuestion(
    val id: String,
    val text: String,
    val options: List<String>,
    val correctAnswer: String
)
