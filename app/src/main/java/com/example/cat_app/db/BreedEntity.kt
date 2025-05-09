package com.example.cat_app.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Breed")
data class BreedEntity(
    @PrimaryKey val id : String,
    val name: String,
    val temperament: String?,
    val origin: String?,
    val description: String?,
    val imageUrl: String?,

    // details
    val childFriendly: Int,
    val dogFriendly: Int,
    val healthIssues: Int,
    val sheddingLevel: Int,
    val strangerFriendly: Int,
    val vocalisation: Int,
    val height: String?,
    val altNames: String?,

    val lifeSpan: String?,
    val weightMetric: String?,     // "3 - 5"
    val weightImperial: String?,    // "7 - 10"
    val adaptability: Int,
    val affectionLevel: Int,
    val energyLevel: Int,
    val intelligence: Int,
    val socialNeeds: Int,
    val grooming: Int,
    val rare: Boolean,
    val wikipediaUrl: String?,

)
