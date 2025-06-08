package com.example.cat_app.features.quiz.data.local

import kotlinx.serialization.Serializable

@Serializable
data class QuizResultDTO(
    val category: Int,
    val nickname: String,
    val result: Double,
    val createdAt: Long
)
