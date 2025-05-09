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
    private val db : AppDatabase
){
    private val dao = db.catImageDao()

    // sta sam ja ovde uopste hteo nzm
    fun observeBreedImages(breedId: String): Flow<List<BreedImage>> =
        dao.imagesByBreed(breedId)
            .map { entities ->
                entities.map { it.toDomain() }
            }

    suspend fun fetchBreedImages(breedId: String) {
        val dtos = api.getBreedImages(breedId)
        val entities = dtos
            .map { it.toDomain(breedId) }
            .map { it.toEntity() }
        dao.insertAll(entities)
    }
}