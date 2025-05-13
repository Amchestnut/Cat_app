package com.example.cat_app.features.allspecies.domain

data class Breed(
    val id: String,
    val name: String,
    val temperament: String? = null,
    val origin: String? = null,
    val description: String? = null,
    val imageUrl: String?,

    // added new
    val lifeSpan: String?,             // example "12 - 15"
    val weightMetric: String?,         // example "3 - 7"
    val weightImperial: String?,
    val height: String?,

    val altNames : String?,

    // 6 traits for a scale 1 to 5
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
