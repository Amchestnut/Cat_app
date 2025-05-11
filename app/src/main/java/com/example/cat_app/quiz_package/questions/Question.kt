package com.example.cat_app.quiz_package.questions

interface Question {
    val id: String            // stable inside one quiz session
    val choices: List<String> // what I show in UI (text or image-urls)

    // Returns points for this answer (0 if incorrect, >0 if correct)
    fun score(answer: String): Int
}