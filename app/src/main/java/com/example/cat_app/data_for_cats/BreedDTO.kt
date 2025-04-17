package com.example.cat_app.data_for_cats

import kotlinx.serialization.Serializable

@Serializable
data class BreedDTO(
    val id: String,
    val name: String,
    val temperament: String? = null,
    val origin: String? = null,
    val description: String? = null,
    val image: ImageDTO? = null
)
