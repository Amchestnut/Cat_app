package com.example.cat_app.breed_gallery

import kotlinx.serialization.Serializable

@Serializable
data class BreedImageDTO(
    val id : String,
    val url : String,

    // ne znam ni da li mi trebaju ova 2
    val width : Int,
    val height : Int,


)
