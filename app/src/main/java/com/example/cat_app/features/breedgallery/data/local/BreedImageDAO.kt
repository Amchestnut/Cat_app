package com.example.cat_app.features.breedgallery.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BreedImageDAO {

    // da bi ovaj query radio, tj ovo "breed_images", moram da ga navedem eksplicitno kao @Entity u PhotoEntity model klasi
    @Query("SELECT * FROM breed_images WHERE breedId = :breedId")
    fun imagesByBreed(breedId: String) : Flow<List<BreedImageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(images : List<BreedImageEntity>)

    @Query("SELECT COUNT(*) FROM breed_images WHERE breedId = :breedId")
    suspend fun countByBreed(breedId: String): Int

}