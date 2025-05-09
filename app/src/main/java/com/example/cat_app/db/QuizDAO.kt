package com.example.cat_app.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizDAO {

    @Upsert
    suspend fun upsert(result: QuizResultEntity)

//    @Query("SELECT * FROM QuizResult ORDER BY dateTime DESC")
//    fun observeAll(): Flow<List<QuizResultEntity>>
}