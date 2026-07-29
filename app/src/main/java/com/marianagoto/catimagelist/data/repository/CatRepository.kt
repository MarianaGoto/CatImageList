package com.marianagoto.catimagelist.data.repository

import android.util.Log
import com.marianagoto.catimagelist.BuildConfig
import com.marianagoto.catimagelist.data.dto.CreateFavoriteRequestDto
import com.marianagoto.catimagelist.data.dto.CreateFavoriteResponseDto
import com.marianagoto.catimagelist.data.dto.FavoriteDto
import com.marianagoto.catimagelist.data.dto.ImageDto
import com.marianagoto.catimagelist.data.network.RetrofitClient
import com.marianagoto.catimagelist.data.remote.api.CatApiService
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class CatRepository(
    private val apiService: CatApiService = RetrofitClient.service
) {
    fun getCatsList(limit: Int = 20): Flow<List<ImageDto>> = flow {
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

    suspend fun getCatById(id: String): Result<ImageDto> {
        return try {
            val cat = apiService.searchCatById(id)
            Result.success(cat)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //addFavoriteCat
    fun addFavoriteCat(image: ImageDto, apiKey: String): Flow<CreateFavoriteResponseDto> = flow{
        emit(apiService.addFavoriteCatById(
            CreateFavoriteRequestDto(imageId = image.id, subId = BuildConfig.SUB_ID),
            apiKey = apiKey)
        )
    }

    //searchFavoriteCats
    fun getFavoriteCatsList(apiKey: String): Flow<List<FavoriteDto>> = flow {
        emit(apiService.searchFavoriteCats(apiKey = apiKey))

    }

    //removeFavoriteCatById
    fun removeFavoriteCat(favoriteId: Int, apiKey: String): Flow<Response<Unit>> = flow {
        emit(apiService.removeFavoriteCatById(favoriteId = favoriteId, apiKey = apiKey))
    }

    fun getFavoriteCatById(favoriteId: Int, apiKey: String): Flow<FavoriteDto> = flow {
        emit(apiService.getFavoriteCatById(favoriteId = favoriteId, apiKey = apiKey))
    }
}
