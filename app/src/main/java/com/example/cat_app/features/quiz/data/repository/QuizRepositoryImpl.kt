package com.example.cat_app.features.quiz.data.repository

import android.util.Log
import com.example.cat_app.core.database.AppDatabase
import com.example.cat_app.core.datastore.UserPreferencesRepository
import com.example.cat_app.features.allspecies.domain.Breed
import com.example.cat_app.features.allspecies.data.mapper.toDomain
import com.example.cat_app.features.leaderboard.data.LeaderboardApiService
import com.example.cat_app.features.leaderboard.data.PostResultRequest
import com.example.cat_app.features.quiz.data.local.QuizResultEntity
import com.example.cat_app.features.quiz.domain.QUESTIONS_PER_GAME
import com.example.cat_app.features.quiz.domain.Question
import com.example.cat_app.features.quiz.domain.QuestionFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizRepositoryImpl @Inject constructor(
    private val db: AppDatabase,
    private val api: LeaderboardApiService,       // retrofit interfejs za POST i GET
    private val userPrefs: UserPreferencesRepository,
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

    override fun history(): Flow<List<QuizResultEntity>> = db.quizResultDao().getAllResults()

    // TODO: najbolji lokalni skor
    override fun bestScore(): Flow<Double> =
        db.quizResultDao().getBestResult().map { it?.result ?: 0.0 }

    // najbolja globalna pozicija (ako je bar jedan objavljen)
    override fun bestRanking(): Flow<Int?> = db.quizResultDao().getBestRanking()

    // samo lokalno čuvanje (pre nego što se objavi)
    override suspend fun saveLocal(result: Double) {
        Log.d("QuizResultRepo", "saveLocal: inserting result=$result")
        db.quizResultDao().insert(
            QuizResultEntity(
                result = result,
                timestamp = System.currentTimeMillis()
            )
        )
        Log.d("QuizResultRepo", "saveLocal: insert complete")
    }

    // objavicu na API i upisacu i u lokalnu bazu sa rankingom!!
    override suspend fun publish(result: Double): Int {
        val nickname = userPrefs.nicknameFlow.first()
        val resp = api.postResult(PostResultRequest(nickname, result, 1))
        val ranking = resp.ranking

        db.quizResultDao().insert(
            QuizResultEntity(
                result = result,
                timestamp = System.currentTimeMillis(),
                published = true,
                ranking = ranking
            )
        )
        return ranking
    }
}