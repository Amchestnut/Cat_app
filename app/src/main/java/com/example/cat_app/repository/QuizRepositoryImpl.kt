package com.example.cat_app.repository

import com.example.cat_app.db.AppDatabase
import com.example.cat_app.domain.Breed
import com.example.cat_app.mappers.toDomain
import com.example.cat_app.quiz_package.QUESTIONS_PER_GAME
import com.example.cat_app.quiz_package.questions.Question
import com.example.cat_app.quiz_package.questions.QuestionFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
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