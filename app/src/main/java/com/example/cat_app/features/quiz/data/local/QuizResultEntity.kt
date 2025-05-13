package com.example.cat_app.features.quiz.data.local

import androidx.room.PrimaryKey

data class QuizResultEntity(
    @PrimaryKey(autoGenerate = true)
    val id : Long = 0,
    val score : Int,
    val total : Int,
    val dateTime : Long = System.currentTimeMillis(),
    val published : Boolean,    // ako je poslat na server, onda TRUE
)
