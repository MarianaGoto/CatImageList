package com.marianagoto.catimagelist.data.remote.api

import com.marianagoto.catimagelist.BuildConfig
import com.marianagoto.catimagelist.data.dto.CatImageResponse
import com.marianagoto.catimagelist.data.dto.FavoriteRequest
import com.marianagoto.catimagelist.data.dto.FavoriteAddResponse
import com.marianagoto.catimagelist.data.dto.FavoriteResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
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
        @Query("has_breeds") hasBreeds: Boolean = true,
        @Header("x-api-key") apiKey: String = API_KEY
    ): List<CatImageResponse>

    @GET("images/{id}")
    suspend fun searchCatById(@Path("id") id: String): CatImageResponse

    @POST("favourites")
    suspend fun addFavoriteCatById(
        @Body favoriteRequest: FavoriteRequest,
        @Header("x-api-key") apiKey: String
    ): FavoriteAddResponse

    @GET("favourites")
    suspend fun searchFavoriteCats(@Header("x-api-key") apiKey: String): List<FavoriteResponse>

    @DELETE("favourites/{favourite_id}")
    suspend fun removeFavoriteCatById(
        @Path("favourite_id") favouriteId: Int,
        @Header("x-api-key") apiKey: String
    ): Response<Unit>
}