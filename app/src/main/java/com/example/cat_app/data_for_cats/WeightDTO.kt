package com.example.cat_app.data_for_cats

import kotlinx.serialization.Serializable

@Serializable
data class WeightDTO(
    val imperial: String? = null,
    val metric:   String? = null
)
