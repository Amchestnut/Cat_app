package com.example.cat_app.data_for_cats

import kotlinx.serialization.Serializable

@Serializable
data class BreedDTO(
    val id: String,
    val name: String,
    val temperament: String? = null,
    val origin: String? = null,
    val description: String? = null,
    val image: ImageDTO? = null,

    // new
    val lifeSpan: String?,             // e.g. "12 - 15"
    val weightMetric: String?,         // e.g. "3 - 7"
    val adaptability: Int,
    val affectionLevel: Int,
    val childFriendly: Int,
    val dogFriendly: Int,
    val energyLevel: Int,
    val grooming: Int,
    val healthIssues: Int,
    val intelligence: Int,
    val sheddingLevel: Int,
    val socialNeeds: Int,
    val strangerFriendly: Int,
    val vocalisation: Int,
    val rare: Boolean,                 // from DTO “rare” field
    val wikipediaUrl: String?          // to open in browser
)
