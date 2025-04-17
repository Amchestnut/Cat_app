package com.example.cat_app.all_species_screen

interface AllSpeciesRepository {

    // suspend jer cemo asinhrono da pozovemo API, i koristimo korutine + suspend da zovemo unutar viewModelScope.launch { }
    suspend fun getAllSpecies(): List<Breed>
}