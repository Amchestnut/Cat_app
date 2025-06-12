package com.example.cat_app.features.allspecies.data.repository

import com.example.cat_app.features.allspecies.domain.Breed
import kotlinx.coroutines.flow.Flow

interface BreedRepository {

    // suspend jer cemo asinhrono da pozovemo API, i koristimo korutine + suspend da zovemo unutar viewModelScope.launch { }
//    suspend fun getAllSpecies(): List<Breed>

    // dodajemo novo za projekat 2
    fun observeAllSpecies(): Flow<List<Breed>>
    fun observeBreed(id: String): Flow<Breed?>
    suspend fun refreshAllSpecies()
}