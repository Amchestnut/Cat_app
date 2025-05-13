package com.example.cat_app.features.allspecies.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class WeightDTO(
    val imperial: String? = null,
    val metric:   String? = null
)
