package com.example.cat_app.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.cat_app.features.breedgallery.data.local.BreedImageDAO
import com.example.cat_app.features.breedgallery.data.local.BreedImageEntity
import com.example.cat_app.features.allspecies.data.local.BreedDAO
import com.example.cat_app.features.allspecies.data.local.BreedEntity
import com.example.cat_app.features.quiz.data.local.QuizResultDAO
import com.example.cat_app.features.quiz.data.local.QuizResultEntity

// Ovde moraju da stoje ::::SVI:::: ENTITETI
@Database(
    entities = [
        BreedEntity::class,
        BreedImageEntity::class,
        QuizResultEntity::class,
    ],
    version = 3,
    exportSchema = false,    // ne radi glupi gradle pa nmg da stavim ono true, set schema location
)

// JAKO BITNO
// Ovde definisem sve DAO INTERFEJSE koje koristim (tj moj repository koristi).
// ne definisem sve metode pojedinacno, nego samo ceo interfejs, a metode (one query, update i to) ce da mi izgenerise ROOM u vreme kompajliranja
// ROOM ce za mene automatski da generise konacnu klasu sa mojim @Query @Insert itd
abstract class AppDatabase : RoomDatabase(){
    abstract fun breedDao() : BreedDAO
    abstract fun catImageDao() : BreedImageDAO
    abstract fun quizResultDao() : QuizResultDAO   // ovo sam na kraju dodao, ovo iz rooma
}