package com.example.cat_app.features.quiz.data.local

import androidx.room.Dao
import androidx.room.Upsert

@Dao
interface QuizDAO {

    @Upsert
    suspend fun upsert(result: QuizResultEntity)

//    @Query("SELECT * FROM QuizResult ORDER BY dateTime DESC")
//    fun observeAll(): Flow<List<QuizResultEntity>>
}