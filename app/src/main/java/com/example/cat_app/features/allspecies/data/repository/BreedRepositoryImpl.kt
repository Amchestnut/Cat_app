package com.example.cat_app.features.allspecies.data.repository

import android.util.Log
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
class BreedRepositoryImpl @Inject constructor(
    private val api : AllSpeciesAPI,
    private val db : AppDatabase,
) : BreedRepository {
    private val TAG = "BreedRepository"

    private val dao = db.breedDao()

    override fun observeAllSpecies() : Flow<List<Breed>> {
        return dao.observeAll().map {
            it.map (BreedEntity::toDomain)
        }
    }

    // tldr: primim BreedEntity?, mapiram u Breed?, vratim Flow<Breed?>, koji je isto COLD, ali u vidu DOMENA
    // pretplacujem se na room DAO, koji vraca FLOW<BreedEntity>, i emitujem SVAKI PUT KADA se taj red u bazi PROMENI
    override fun observeBreed(id: String): Flow<Breed?> =
        dao.observeById(id)
            .map { it?.toDomain() }    // mapiram dobijeni BREED ENTITY u BREED DOMAIN


/*
Mogao sam da uradim i ovo:
override suspend fun refreshAllSpecies() = withContext(Dispatchers.IO) {
  val dtos = api.getAllBreeds()
  dao.upsertAll(dtos.map { it.toEntity() })
}

ali nece doprineti nista, jer retrofit suspend funkcije same izvrsavaju HTTP pozive na pozadinskom threadu (okHTTP pool thread-ova)
OkHTTP ima svoj Dispatcher koji drzi pool java niti, i na jednoj od njih izvrsava mrezni request
 */
    override suspend fun refreshAllSpecies() {
        Log.d(TAG, "refreshAllSpecies: starting fetch from network…")
        try {
            val dtos = api.getAllBreeds()
            Log.d(TAG, "refreshAllSpecies: API returned ${dtos.size} breeds")

            val remote = api.getAllBreeds().map {
                it.toEntity()
            }
            dao.upsertAll(remote)
            Log.d(TAG, "refreshAllSpecies: DB upsert complete")
        } catch (e: Exception) {
            Log.e(TAG, "refreshAllSpecies: error fetching breeds", e)
            throw e
        }
    }
}

