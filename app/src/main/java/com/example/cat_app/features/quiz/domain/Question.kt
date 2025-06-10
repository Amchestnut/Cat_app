package com.example.cat_app.features.quiz.domain

interface Question {
    val id: String            // stable inside one quiz session
    val questionText: String
    val imageUrl: String
    val choices: List<String> // what I show in UI (text or image-urls)

    // Returns points for this answer (0 if incorrect, 5 if correct)
    fun score(answer: String): Int
}