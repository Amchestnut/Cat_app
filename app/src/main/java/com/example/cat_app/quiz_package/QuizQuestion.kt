package com.example.cat_app.quiz_package

data class QuizQuestion(
    val id: String,
    val text: String,
    val options: List<String>,
    val correctAnswer: String
)
