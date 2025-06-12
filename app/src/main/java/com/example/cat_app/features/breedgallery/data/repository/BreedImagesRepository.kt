package com.example.cat_app.features.breedgallery.data.repository

import com.example.cat_app.features.breedgallery.domain.BreedImage
import com.example.cat_app.features.breedgallery.data.mapper.toDomain
import com.example.cat_app.features.breedgallery.data.mapper.toEntity
import com.example.cat_app.features.allspecies.data.remote.AllSpeciesAPI
import com.example.cat_app.core.database.AppDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

interface BreedImagesRepository {
    fun getAllImagesForThisBreed(breedId: String): Flow<List<BreedImage>>
    suspend fun fetchAndCacheBreedImages(breedId: String)
}