package com.example.cat_app.all_species_screen

import com.example.cat_app.data_for_cats.AllSpeciesAPI
import com.example.cat_app.domain.Breed
import javax.inject.Inject
import javax.inject.Singleton

// @Inject constructor(): Hilt može da “automatski” kreira ovu klasu (jer zna da joj ne trebaju nikakvi parametri, ili bi ih uzeo iz drugih bindinga).
@Singleton      // redudantno jer ga imam i na @provides u modulu, ali sto da ne, neka bude jasnije.
class AllSpeciesRepositoryRetrofit @Inject constructor(
    private val api: AllSpeciesAPI
) : AllSpeciesRepository {   // INJECT: mesto gde HILT ubacuje zavisnost


    override suspend fun getAllSpecies(): List<Breed> {
        // 1) Dohvati raw DTO-e
        val dtoList = api.getAllBreeds()

        // 2) Mapiraj ih u domain model
        return dtoList.map { dto ->
            Breed(
                id = dto.id,
                name = dto.name,
                temperament = dto.temperament,
                origin = dto.origin,
                description = dto.description,
                imageUrl = dto.image?.url,

//                lifeSpan         = dto.life_span,
//                weightMetric     = dto.weightMetric,

                altNames = dto.altNames,
                adaptability     = dto.adaptability,
                affectionLevel   = dto.affectionLevel,
                childFriendly    = dto.childFriendly,
                dogFriendly      = dto.dogFriendly,
                energyLevel      = dto.energyLevel,

                grooming         = dto.grooming,
                healthIssues     = dto.healthIssues,
                intelligence     = dto.intelligence,
                sheddingLevel    = dto.sheddingLevel,
                socialNeeds      = dto.socialNeeds,
                strangerFriendly = dto.strangerFriendly,
                vocalisation     = dto.vocalisation,
                rare             = dto.rare == 1,
                wikipediaUrl     = dto.wikipediaUrl
            )
        }
    }

}