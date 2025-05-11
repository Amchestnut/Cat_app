package com.example.cat_app.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.cat_app.breed_gallery.BreedImageDAO
import com.example.cat_app.breed_gallery.BreedImageEntity

// Ovde moraju da stoje ::::SVI:::: ENTITETI
@Database(
    entities = [
        BreedEntity::class,
        BreedImageEntity::class,
    ],
    version = 2,
    exportSchema = false,    // ne radi glupi gradle pa nmg da stavim ono true, set schema location
)

// JAKO BITNO
// Ovde definisem sve DAO INTERFEJSE koje koristim (tj moj repository koristi).
// ne definisem sve metode pojedinacno, nego samo ceo interfejs, a metode (one query, update i to) ce da mi izgenerise ROOM u vreme kompajliranja
// ROOM ce za mene automatski da generise konacnu klasu sa mojim @Query @Insert itd
abstract class AppDatabase : RoomDatabase(){
    abstract fun breedDao() : BreedDAO
    abstract fun catImageDao() : BreedImageDAO
//    abstract fun quizDao() : QuizDAO    ne jos
}