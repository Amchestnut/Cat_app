package com.example.cat_app.features.quiz.data.repository

import android.util.Log
import com.example.cat_app.core.database.AppDatabase
import com.example.cat_app.core.datastore.UserPreferencesRepository
import com.example.cat_app.features.allspecies.domain.Breed
import com.example.cat_app.features.allspecies.data.mapper.toDomain
import com.example.cat_app.features.leaderboard.data.LeaderboardApi
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
    private val api: LeaderboardApi,       // retrofit interfejs za POST i GET
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

    override fun history(): Flow<List<QuizResultEntity>> {
        return db.quizResultDao().getAllResults()
    }

    override fun bestScore(): Flow<Double> {
        return db.quizResultDao().getBestResult().map { it?.result ?: 0.0 }
    }

    // najbolja globalna pozicija (ako je bar jedan objavljen)
    override fun bestRanking(): Flow<Int?> {
        return db.quizResultDao().getBestRanking()
    }

    // samo lokalno cuvanje (pre nego što se objavi)
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
    /**
     * Zovem ovu funkciju da bi objavio rezultat (double) na globalni leaderboard. To mi je jedini parametar funkcije.
     * INT return vrednost, jer ova funkcija treba da mi vrati moj NOVI RANKING na globalnoj leaderboard listi.
     */
    override suspend fun publish(result: Double): Int {
        val nickname = userPrefs.nicknameFlow.first()

        // dobicu response koji je tipa "PostResultResponse", tu imam 1)QuizResultDTO i 2)ranking
        val response = api.postResult(PostResultRequest(nickname, result, 1))
        val ranking = response.ranking

        // Dohvatim prethodno ubacen lokalni zapis. Mozda je ovo trebalo malo bolje, da prvo DTO pretvorim u DOMAIN, pa DOMAIN u ENTITY, tj da azuriram entity sa ovim ID,
        val latest = db.quizResultDao().getLatestResult()

        if (latest != null) {
            // Napravi kopiju sa published=true i ranking za svaki slucaj
            val updated = latest.copy(
                published = true,
                ranking = ranking
            )
            // 4) Azuriram ovaj entitet u bazi (UPDATE)
            db.quizResultDao().update(updated)
        } else {
            // fallback: ako iz nekog razloga nema prethodnog, ubaci novi
            db.quizResultDao().insert(
                QuizResultEntity(
                    result    = result,
                    timestamp = System.currentTimeMillis(),
                    published = true,
                    ranking   = ranking
                )
            )
        }

        return ranking
    }
}