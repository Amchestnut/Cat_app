package com.example.cat_app.core.network

import com.example.cat_app.features.allspecies.data.remote.AllSpeciesAPI
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson() : Json = Json {
        ignoreUnknownKeys = true
    }

    // Ovo mi pravi "oKHttpClient-a, sa intercepterom koji u svaki HTTP zahtev dodaje header "x-api-key" i jos jedan za logovanje (BODY -> pun zapis tela zahteva)
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val req = chain.request().newBuilder()
                    .addHeader("x-api-key", "live_Nj3uX1486zgD91CnsN7B9banbDlAusGRexPFxVInL3FAY0HX8bKkknfT2q6i28B7")
                    .build()
                chain.proceed(req)
            }
            .addInterceptor(HttpLoggingInterceptor().apply { level =
                HttpLoggingInterceptor.Level.BODY   // importovao sam BODY,
            })
            .build()


    // Konfigurisem bazni URL, ubacujem taj "client" i JSON converter
    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient, json: Json): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://api.thecatapi.com/v1/")
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()


    // ovim pozivom "retrofit.create(...) dobijam IMPLEMENTACIJU interfejsa "ALLSpeciesAPI" koji sam napravio.
    // hilt sada zna da umetne svuda interfejs gde da @Inject zatrazi.
    @Provides
    @Singleton
    fun provideAllSpeciesApi(retrofit: Retrofit): AllSpeciesAPI =
        retrofit.create(AllSpeciesAPI::class.java)
}
