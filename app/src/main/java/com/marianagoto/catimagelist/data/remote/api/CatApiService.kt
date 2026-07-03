package com.marianagoto.catimagelist.data.remote.api

import com.marianagoto.catimagelist.BuildConfig
import com.marianagoto.catimagelist.data.dto.CatImageResponse
import com.marianagoto.catimagelist.data.dto.CreateFavouriteRequestDto
import com.marianagoto.catimagelist.data.dto.CreateFavouriteResponseDto
import com.marianagoto.catimagelist.data.dto.FavoriteDto
import com.marianagoto.catimagelist.data.dto.FavoriteResponse
import com.marianagoto.catimagelist.data.dto.ImageDto
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
    ): List<ImageDto>

    @GET("images/{id}")
    suspend fun searchCatById(@Path("id") id: String): ImageDto

    @POST("favourites")
    suspend fun addFavoriteCatById(
        @Body favoriteRequest: CreateFavouriteRequestDto,
        @Header("x-api-key") apiKey: String
    ): CreateFavouriteResponseDto

    @GET("favourites")
    suspend fun searchFavoriteCats(@Header("x-api-key") apiKey: String): List<FavoriteDto>

    @DELETE("favourites/{favourite_id}")
    suspend fun removeFavoriteCatById(
        @Path("favourite_id") favouriteId: Int,
        @Header("x-api-key") apiKey: String
    ): Response<Unit>

    @GET("favourites/{favourite_id}")
    suspend fun getFavoriteCatById(
        @Path("favourite_id") favouriteId: Int,
        @Header("x-api-key") apiKey: String
    ): FavoriteDto
}