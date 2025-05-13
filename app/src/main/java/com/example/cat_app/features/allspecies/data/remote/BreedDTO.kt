package com.example.cat_app.features.allspecies.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BreedDTO(
    val id: String,
    val name: String,
    val temperament: String? = null,
    val origin: String? = null,
    val description: String? = null,
    val image: ImageDTO? = null,


    // Detailed information
    @SerialName("life_span")
    val lifeSpan: String? = null,

    val weight: WeightDTO? = null,

    val height: String? = null,

    @SerialName("alt_names")
    val altNames: String? = null,

    val adaptability: Int,

    @SerialName("affection_level")      // without this, kotlin will look for JSON key called "AffectionLevel" but there is only "affection_level", so i need to use this.
    val affectionLevel: Int,

    @SerialName("child_friendly")
    val childFriendly: Int,

    @SerialName("dog_friendly")
    val dogFriendly: Int,

    @SerialName("energy_level")
    val energyLevel: Int,

    val grooming: Int,

    @SerialName("health_issues")
    val healthIssues: Int,

    val intelligence: Int,

    @SerialName("shedding_level")
    val sheddingLevel: Int,

    @SerialName("social_needs")
    val socialNeeds: Int,

    @SerialName("stranger_friendly")
    val strangerFriendly: Int,

    val vocalisation: Int,

    val rare: Int,                 // from DTO “rare” field, 1 or 0

    @SerialName("wikipedia_url")
    val wikipediaUrl: String? = null          // to open in browser
)
