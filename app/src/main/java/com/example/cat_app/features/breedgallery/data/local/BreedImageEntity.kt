package com.example.cat_app.features.breedgallery.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "breed_images",
)
data class BreedImageEntity (
    val breedId: String,
    @PrimaryKey
    val imageId: String,
    val url: String,
    val width: Int,
    val height: Int
)
