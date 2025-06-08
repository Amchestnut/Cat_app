package com.example.cat_app.features.leaderboard.data

import com.example.cat_app.features.quiz.data.local.QuizResultDTO
import kotlinx.serialization.Serializable

@Serializable
data class PostResultResponse(
    val result: QuizResultDTO,
    val ranking: Int
)

