package com.example.cat_app.features.leaderboard.data.network

import com.example.cat_app.features.leaderboard.data.LeaderboardApi
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
        client: OkHttpClient,  // iskoristicu ponovo moj OkHttpClient iz NetworkModule
        json: Json       // iskoristicu ponovo moj JSON iz NetworkModule
    ): LeaderboardApi {
        // Pravim retrofit, samo API servis, ne expozujem Retrofit
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val leaderboardClient = client.newBuilder()
            // Ako bi mi trebao neki drugaciji INTERCEPTOR za leaderboard, dodacu ga ovde:
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://rma.finlab.rs/")
            .client(leaderboardClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

        return retrofit.create(LeaderboardApi::class.java)
    }
}