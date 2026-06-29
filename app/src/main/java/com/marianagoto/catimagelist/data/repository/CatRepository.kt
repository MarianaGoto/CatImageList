package com.marianagoto.catimagelist.data.repository

import com.marianagoto.catimagelist.data.dto.CatImageResponse
import com.marianagoto.catimagelist.data.dto.FavoriteAddResponse
import com.marianagoto.catimagelist.data.dto.FavoriteRequest
import com.marianagoto.catimagelist.data.dto.FavoriteResponse
import com.marianagoto.catimagelist.data.network.RetrofitClient
import com.marianagoto.catimagelist.data.remote.api.CatApiService
import retrofit2.Response

class CatRepository(
    private val apiService: CatApiService = RetrofitClient.service
) {
    suspend fun getCatsList(limit: Int = 20): Result<List<CatImageResponse>> {
        return try {
            // 1. Buscar lista básica de gatos
            val cats = apiService.searchCats(limit = limit, hasBreeds = true)

            // 2. Detalhes de cada gato
            val detailedCats = cats.map { cat ->
                getCatDetails(cat)
            }

            Result.success(detailedCats)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun getCatDetails(cat: CatImageResponse): CatImageResponse {
        return try {
            apiService.searchCatById(cat.id)
        } catch (e: Exception) {
            cat
        }
    }

    suspend fun getCatById(id: String): Result<CatImageResponse> {
        return try {
            val cat = apiService.searchCatById(id)
            Result.success(cat)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //addFavoriteCat

    suspend fun addFavoriteCat(favoriteRequest: FavoriteRequest, apiKey: String): Result<FavoriteAddResponse>{
        return try {
            // 1. Buscar lista básica de gatos
            val catFavorite = apiService.addFavoriteCatById(favoriteRequest = favoriteRequest, apiKey = apiKey)
            Result.success(catFavorite)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //searchFavoriteCats
    suspend fun getFavoriteCatsList(apiKey: String): Result<List<FavoriteResponse>> {
        return try {
            val catFavoriteList = apiService.searchFavoriteCats(apiKey = apiKey)
            Result.success(catFavoriteList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //removeFavoriteCatById
    suspend fun removeFavoriteCat(favouriteId: Int, apiKey: String):  Result<Response<Unit>>{
        return try {
            val catFavoriteList = apiService.removeFavoriteCatById(favouriteId = favouriteId, apiKey = apiKey)
            Result.success(catFavoriteList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
