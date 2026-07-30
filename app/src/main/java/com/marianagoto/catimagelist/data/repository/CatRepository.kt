package com.marianagoto.catimagelist.data.repository

import android.util.Log
import com.marianagoto.catimagelist.data.dto.CatImageResponse
import com.marianagoto.catimagelist.data.dto.FavoriteAddResponse
import com.marianagoto.catimagelist.data.dto.FavoriteRequest
import com.marianagoto.catimagelist.data.dto.FavoriteResponse
import com.marianagoto.catimagelist.data.network.RetrofitClient
import com.marianagoto.catimagelist.data.remote.api.CatApiService
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import retrofit2.Response

class CatRepository(
    private val apiService: CatApiService = RetrofitClient.service
) {
    fun getCatsList(limit: Int = 20): Flow<List<CatImageResponse>> = flow {
        val initialCats = apiService.searchCats(limit = limit, hasBreeds = true)
        val detailedCats = coroutineScope {
            initialCats.map { cat ->
                async {
                    try {
                        apiService.searchCatById(cat.id)
                    } catch (e: Exception) {
                        Log.e("", "$e")
                        cat /// retorna 'cat' para manter o tipo List<CatImageResponse>
                    }
                }
            }.awaitAll()
        }
        emit(detailedCats)
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
    suspend fun removeFavoriteCat(favouriteId: Int, apiKey: String): Result<Unit>{
        return try {
            val response =
                apiService.removeFavoriteCatById(favouriteId = favouriteId, apiKey = apiKey)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(HttpException(response))
            }
        }catch (exception: Exception) {
                Result.failure(exception)
            }
    }
}
