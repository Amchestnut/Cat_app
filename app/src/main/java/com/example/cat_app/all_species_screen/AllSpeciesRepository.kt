package com.example.cat_app.all_species_screen

import com.example.cat_app.domain.Breed

interface AllSpeciesRepository {

    // suspend jer cemo asinhrono da pozovemo API, i koristimo korutine + suspend da zovemo unutar viewModelScope.launch { }
    suspend fun getAllSpecies(): List<Breed>
}