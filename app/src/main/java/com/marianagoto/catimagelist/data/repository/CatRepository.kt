package com.marianagoto.catimagelist.data.repository

import com.marianagoto.catimagelist.data.network.RetrofitClient
import com.marianagoto.catimagelist.data.remote.api.CatApiService
import com.marianagoto.catimagelist.domain.model.CatImage

class CatRepository(
    private val apiService: CatApiService = RetrofitClient.service
) {
    suspend fun getCatsList(limit: Int = 20): Result<List<CatImage>> {
        return try {
            // 1. Buscar lista básica de gatos
            val cats = apiService.searchCats(limit = limit, hasBreeds = 1)

            // 2. Detalhes de cada gato
            val detailedCats = cats.map { cat ->
                getCatDetails(cat)
            }

            Result.success(detailedCats)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun getCatDetails(cat: CatImage): CatImage {
        return try {
            apiService.searchCatById(cat.id)
        } catch (e: Exception) {
            cat
        }
    }

    suspend fun getCatById(id: String): Result<CatImage> {
        return try {
            val cat = apiService.searchCatById(id)
            Result.success(cat)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
