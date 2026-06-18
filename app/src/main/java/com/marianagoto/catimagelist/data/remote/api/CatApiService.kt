package com.marianagoto.catimagelist.data.remote.api

import com.marianagoto.catimagelist.domain.model.CatImage
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

const val DEMO_API_KEY = "live_qaRv5a18HnM6T0RvWXscDQZ79JaFJEKWm2kNMOzfmJHAD1evhEMtIeBx9WIfajyW"

interface CatApiService {
    @GET( "images/search")
    suspend fun searchCats(
        @Query("limit") limit: Int = 20,
        @Query("has_breeds") hasBreeds: Int = 1,
        @Header("x-api-key") apiKey: String = DEMO_API_KEY
    ): List<CatImage>

    @GET("images/{id}")
    suspend fun searchCatById(@Path("id") id: String): CatImage

}