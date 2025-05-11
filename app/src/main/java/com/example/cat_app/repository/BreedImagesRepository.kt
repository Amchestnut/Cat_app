package com.example.cat_app.repository

import com.example.cat_app.breed_gallery.BreedImage
import com.example.cat_app.breed_gallery.toDomain
import com.example.cat_app.breed_gallery.toEntity
import com.example.cat_app.data_for_cats.AllSpeciesAPI
import com.example.cat_app.db.AppDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BreedImagesRepository @Inject constructor(
    private val api : AllSpeciesAPI,
    private val db : AppDatabase        // za upisivanje u databazu
){
    private val dao = db.catImageDao()

    // Vraca mi sve slike od macke sa datim ID
    // Mapiram dobijeni niz breedImageEntity-ja u moj domain model BreedImage pozivom "toDomain()"
    fun getAllImagesForThisBreed(breedId: String): Flow<List<BreedImage>> {
        return dao.imagesByBreed(breedId)
            .map { entities ->
                entities.map { it.toDomain() }
            }
    }


    // First time we fetch from the API
    // Second and other times we get it from ROOM database cached
    suspend fun fetchAndCacheBreedImages(breedId: String) {
        if (dao.countByBreed(breedId) == 0) {        // samo prvi put!
            val dtos = api.getBreedImages(breedId)
            val entities = dtos.map {
                it.toDomain(breedId).toEntity()
            }
            // cache everything in database
            dao.insertAll(entities)
        }
    }

}