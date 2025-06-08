package com.example.cat_app.features.leaderboard.data.network

import com.example.cat_app.features.leaderboard.data.LeaderboardApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.MediaType.Companion.toMediaType

@Module
@InstallIn(SingletonComponent::class)
object LeaderboardNetworkModule {

    @Provides
    @Singleton
    fun provideLeaderboardApi(
        client: OkHttpClient,  // reuses your existing OkHttpClient from NetworkModule
        json: Json             // reuses your existing Json from NetworkModule
    ): LeaderboardApiService {
        // Build a one-off Retrofit, create only the API service, never expose Retrofit itself.
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val leaderboardClient = client.newBuilder()
            // If you *do* need a different interceptor for leaderboard, add it here
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://rma.finlab.rs/")
            .client(leaderboardClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

        return retrofit.create(LeaderboardApiService::class.java)
    }
}