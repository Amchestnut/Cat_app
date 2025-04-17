package com.example.cat_app.data_for_cats

import retrofit2.http.GET

interface AllSpeciesAPI {
    @GET("breeds")
    suspend fun getAllBreeds(): List<BreedDTO>
}