package com.example.cat_app.features.allspecies.data.local

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

    // ZANIMLJIVO:
    // svaki put kada se u tabeli BREED promeni red sa ovim "id", ROOM emituje novu vrednost, mi dobijamo ovaj Flow<BreedEntity?> (mozda je null pa -> ?)
    // ponasa se kao "cold flow", kada neko pozove sa collect ili first
    // Zasto se to desava? zato sto "InvalidationTracker" aktivira sve registrovane listenere za tu tabelu
    // Bukvalno Room ponovo izvrsava ovaj SQL UPIT za svaki aktivan collect.
    @Query("SELECT * FROM Breed WHERE id = :breedId")
    fun observeById(breedId : String) : Flow<BreedEntity?>

}