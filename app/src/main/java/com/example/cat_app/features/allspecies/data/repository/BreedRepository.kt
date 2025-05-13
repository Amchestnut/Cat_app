package com.example.cat_app.features.allspecies.data.repository

import androidx.room.withTransaction
import com.example.cat_app.features.allspecies.data.remote.AllSpeciesAPI
import com.example.cat_app.core.database.AppDatabase
import com.example.cat_app.features.allspecies.data.local.BreedEntity
import com.example.cat_app.features.allspecies.domain.Breed
import com.example.cat_app.features.allspecies.data.mapper.toDomain
import com.example.cat_app.features.allspecies.data.mapper.toEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BreedRepository @Inject constructor(
    private val api : AllSpeciesAPI,
    private val db : AppDatabase,
) : AllSpeciesRepository {

    private val dao = db.breedDao()

    override fun observeAllSpecies() : Flow<List<Breed>> {
        return dao.observeAll().map {
            it.map (BreedEntity::toDomain)
        }
    }

    override fun observeBreed(id: String): Flow<Breed?> =
        dao.observeById(id)
            .map { it?.toDomain() }


    // refresh, mozda nekad bude zatrebao
    override suspend fun refreshAllSpecies() = withContext(Dispatchers.IO) {
        // 1) fetch sa interneta
        val remote = api.getAllBreeds().map {
            it.toEntity()
        }
        // 2) upsert u bazu u transakciji
        db.withTransaction {
            dao.upsertAll(remote)
        }
    }
}