package com.example.cat_app.features.quiz.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz_results")
data class QuizResultEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val result: Double,
    val timestamp: Long,
    val published: Boolean = false,        // da li je objavljeno na globalnoj listi
    val ranking: Int? = null               // pozicija vraćena od API-ja
)
