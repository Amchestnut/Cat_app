package com.example.cat_app.features.leaderboard.data

import kotlinx.serialization.Serializable

@Serializable
data class PostResultRequest(       // iskljucivo sluzi za slanje requesta na leaderboard, da ne bi pisali tamo 3 polja u BODY.
    val nickname: String,
    val result: Double,
    val category: Int,

)