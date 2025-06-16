package com.example.cat_app.features.quiz.data.local

import kotlinx.serialization.Serializable

@Serializable
data class QuizResultDTO(       /// u sustini, sluzi da bi primio rezultat
    val nickname: String,
    val result: Double,
    val category: Int,
    val createdAt: Long
)
