package com.marianagoto.catimagelist.data.remote.api

import com.marianagoto.catimagelist.domain.model.CatImage
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface CatApiService {
    @GET( "images/search")
    suspend fun searchCats(
        @Query("limit") limit: Int = 20,
        @Query("has_breeds") hasBreeds: Int = 1
    ): List<CatImage>

    @GET("images/{id}")
    suspend fun searchCatById(@Path("id") id: String): CatImage

}