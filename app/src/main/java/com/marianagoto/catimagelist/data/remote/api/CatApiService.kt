package com.marianagoto.catimagelist.data.remote.api

import com.marianagoto.catimagelist.BuildConfig
import com.marianagoto.catimagelist.domain.model.CatImage
import com.marianagoto.catimagelist.domain.model.FavoriteRequest
import com.marianagoto.catimagelist.domain.model.FavoriteResponseAdd
import com.marianagoto.catimagelist.domain.model.FavoriteResponseList
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

const val API_KEY = BuildConfig.API_KEY

interface CatApiService {
    @GET( "images/search")
    suspend fun searchCats(
        @Query("limit") limit: Int = 20,
        @Query("has_breeds") hasBreeds: Int = 1,
        @Header("x-api-key") apiKey: String = API_KEY
    ): List<CatImage>

    @GET("images/{id}")
    suspend fun searchCatById(@Path("id") id: String): CatImage

    @POST("favourites")
    suspend fun addFavoriteCatById(
        @Body favoriteRequest: FavoriteRequest,
        @Header("x-api-key") apiKey: String
    ): FavoriteResponseAdd

    @GET("favourites")
    suspend fun searchFavoriteCats(@Header("x-api-key") apiKey: String): List<FavoriteResponseList>
}