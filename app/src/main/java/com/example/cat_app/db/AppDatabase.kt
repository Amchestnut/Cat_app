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

abstract class AppDatabase : RoomDatabase(){
    abstract fun breedDao() : BreedDAO
    abstract fun catImageDao() : BreedImageDAO
//    abstract fun quizDao() : QuizDAO    ne jos
}