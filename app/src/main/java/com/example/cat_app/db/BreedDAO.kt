package com.example.cat_app.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface BreedDAO {

    @Upsert // Prvo ce probati Insert, a ako primarni kljuc vec postoji u bazi, uradice Update
    suspend fun upsertAll(breeds : List<BreedEntity>)

    @Query("SELECT * FROM Breed ORDER BY name")
    fun observeAll() : Flow<List<BreedEntity>>

    @Query("SELECT * FROM Breed WHERE id = :breedId")
    fun observeById(breedId : String) : Flow<BreedEntity?>

}