package com.example.cat_app.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [BreedEntity::class],
    version = 1,
    exportSchema = false,    // ne radi glupi gradle pa nmg da stavim ono true, set schema location
)

abstract class AppDatabase : RoomDatabase(){
    abstract fun breedDao() : BreedDAO
//    abstract fun quizDao() : QuizDAO    ne jos
}