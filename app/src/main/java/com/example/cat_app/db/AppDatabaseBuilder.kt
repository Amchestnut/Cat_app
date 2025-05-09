package com.example.cat_app.db

import android.content.Context
import androidx.room.Room
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AppDatabaseBuilder @Inject constructor(
    @ApplicationContext
    private val context : Context,
) {
    fun build(): AppDatabase{
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "cat_app_db",
        )
            .fallbackToDestructiveMigration(dropAllTables = true)
            // ovde bi isle one migracije sa verzije 1 na verziju 2
            .build()
    }
}