package com.example.cat_app.all_species_screen

import javax.inject.Inject
import javax.inject.Singleton

// @Inject constructor(): Hilt može da “automatski” kreira ovu klasu (jer zna da joj ne trebaju nikakvi parametri, ili bi ih uzeo iz drugih bindinga).
@Singleton      // redudantno jer ga imam i na @provides u modulu, ali sto da ne, neka bude jasnije.
class AllSpeciesRepositoryInMemory @Inject constructor() : AllSpeciesRepository {   // INJECT: mesto gde HILT ubacuje zavisnost


    override suspend fun getAllSpecies(): List<Int> {
        // dummy podaci; kasnije ovde ide Retrofit poziv
        return listOf(1,2,3,4,5,6)
    }

}