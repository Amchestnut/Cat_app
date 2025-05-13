package com.example.cat_app.features.quiz.data.repository

import com.example.cat_app.core.database.AppDatabase
import com.example.cat_app.features.allspecies.domain.Breed
import com.example.cat_app.features.allspecies.data.mapper.toDomain
import com.example.cat_app.features.quiz.domain.QUESTIONS_PER_GAME
import com.example.cat_app.features.quiz.domain.Question
import com.example.cat_app.features.quiz.domain.QuestionFactory
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizRepositoryImpl @Inject constructor(
    private val db: AppDatabase
) : QuizRepository {

    override suspend fun generateQuiz(): List<Question> {
        val entities = db.breedDao()   // npr neka: suspend fun @Query("SELECT * FROM BreedEntity")
            .observeAll()           // Flow<List<BreedEntity>>
            .first()                // uzmem prvi (i jedini) emit

        val breeds : List<Breed> = entities
            .map { it.toDomain() }

        return QuestionFactory
            .fromBreeds(breeds)
            .shuffled()
            .take(QUESTIONS_PER_GAME)
    }

    override suspend fun postScore(score: Int) {
        // TODO – dodaj Leaderboard API posle kad ga budes imao
    }
}