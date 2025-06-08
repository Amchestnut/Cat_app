package com.example.cat_app.core.di

import com.example.cat_app.core.database.AppDatabase
import com.example.cat_app.core.database.AppDatabaseBuilder
import com.example.cat_app.features.quiz.data.local.QuizResultDAO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDb(builder: AppDatabaseBuilder) : AppDatabase {
        return builder.build()
    }

//    @Provides
//    fun provideQuizResultDao(db: AppDatabase): QuizResultDAO =
//        db.quizResultDao()

}