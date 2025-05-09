package com.example.cat_app.data_for_cats

import retrofit2.http.Query
import com.example.cat_app.breed_gallery.BreedImageDTO
import retrofit2.http.GET

interface AllSpeciesAPI {
    @GET("breeds")
    suspend fun getAllBreeds(): List<BreedDTO>

    @GET("images/search")
    suspend fun getBreedImages(
        @Query("breed_id")
        breedId : String,
        @Query("limit")
        limit: Int = 50
    ) : List<BreedImageDTO>
}