package com.example.cat_app.features.quiz.domain

interface Question {
    val id: String            // stable inside one quiz session
    val questionText: String
    val imageUrl: String
    val choices: List<String> // what I show in UI (text or image-urls)
    val correctChoice: String
}