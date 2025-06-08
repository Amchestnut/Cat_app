package com.example.cat_app.features.quiz.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizResultDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(result: QuizResultEntity)

    @Query("SELECT * FROM quiz_results ORDER BY timestamp DESC")
    fun getAllResults(): Flow<List<QuizResultEntity>>

    @Query("SELECT * FROM quiz_results ORDER BY result DESC LIMIT 1")
    fun getBestResult(): Flow<QuizResultEntity?>

    @Query("SELECT MIN(ranking) FROM quiz_results WHERE published = 1")
    fun getBestRanking(): Flow<Int?>

}