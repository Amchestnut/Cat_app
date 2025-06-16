package com.example.cat_app.features.leaderboard.data

import kotlinx.serialization.Serializable

@Serializable
data class PostResultRequest(
    val nickname: String,
    val result: Double,
    val category: Int,

)