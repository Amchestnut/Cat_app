package com.example.cat_app.features.leaderboard.data

import com.example.cat_app.core.database.AppDatabase
import com.example.cat_app.core.datastore.UserPreferencesRepository
import com.example.cat_app.features.quiz.data.local.QuizResultDTO
import com.example.cat_app.features.quiz.data.local.QuizResultEntity
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LeaderboardRepositoryImpl @Inject constructor(
    private val api: LeaderboardApi,
    private val db: AppDatabase,
    private val userPrefs: UserPreferencesRepository,
) : LeaderboardRepository {

    override suspend fun getLeaderboard(category: Int): List<QuizResultDTO> {
        return api.getLeaderboard(category)
    }

}