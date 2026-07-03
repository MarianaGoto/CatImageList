package com.marianagoto.catimagelist.domain.usecase

import com.marianagoto.catimagelist.BuildConfig
import com.marianagoto.catimagelist.data.dto.FavoriteRichResponse
import com.marianagoto.catimagelist.data.repository.CatRepository
import com.marianagoto.catimagelist.domain.mapper.catImageResponseAndFavoriteResponseToFavoriteRichResponse
import com.marianagoto.catimagelist.domain.mapper.imageDtoAndFavoriteDtoToFavoriteRichVO
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.collections.filterNotNull

class GetFavoriteCatsUseCase(private val repository: CatRepository) {
    fun getFavoriteRichCats():  Flow<List<FavoriteRichResponse>> = flow {
        // 1. Busca a lista de favoritos inicial
        val favoritesResult = repository.getFavoriteCatsList(apiKey = BuildConfig.API_KEY)

        favoritesResult.fold(onSuccess = { favoriteResponseList ->
            // 2. Usamos coroutineScope para permitir chamadas paralelas (async)
            val detailedList = coroutineScope {
                favoriteResponseList.map { favoriteResponse ->
                    async {
                        // Busca detalhes de cada gato individualmente
                        val catDetailResult = repository.getCatById(favoriteResponse.imageId)

                        catDetailResult.fold(onSuccess = { catImageResponse ->
                            // Mapeia para o objeto rico (VO/DTO consolidado)
                            imageDtoAndFavoriteDtoToFavoriteRichVO(
                                imageDto = catImageResponse,
                                favoriteDto = favoriteResponse
                            )

//                            catImageResponseAndFavoriteResponseToFavoriteRichResponse(
//                                catImageResponse = catImageResponse,
//                                favoriteResponse = favoriteResponse
//                            )
                        }, onFailure = {
                            null // Se falhar um gato, retornamos null para filtrar depois
                        })
                    }
                }.awaitAll().filterNotNull() // Aguarda todos e remove os que falharam
            }

            // 3. Emite a lista final consolidada
            emit(detailedList)
        }, onFailure = {
            // Em caso de falha na lista principal, você pode lançar a exceção
            // para ser tratada pelo .catch no ViewModel ou emitir uma lista vazia

            throw it
        })
    }
}





//    suspend fun getFavoriteRichCats() :Result<List<FavoriteRichResponse>>{
//        try {
//            val result = repository.getFavoriteCatsList(apiKey = BuildConfig.API_KEY)
//            result.fold(
//                onSuccess = { favoriteResponseList ->
//                    favoriteResponseList.forEach { favoriteResponse ->
//                        async {
//                            val result = repository.getCatById(favoriteResponse.imageId)
//                            result.fold(
//                                onSuccess = { catImageResponse ->
//                                    val favoriteVO =
//                                        catImageResponseAndFavoriteResponseToFavoriteRichResponse(
//                                            catImageResponse = catImageResponse,
//                                            favoriteResponse = favoriteResponse
//                                        )
//                                    return Result.success(favoriteVO)
//                                },
//                                onFailure = { e ->
//                                    return Result.failure(e)
//                                }
//                            )
//                        }.awaitAll()
//                    }
//                },
//                onFailure = { e ->
//                    return Result.failure(e)
//                }
//            )
//        } catch (e: Exception) {
//            return Result.failure(e)
//        }
//    }
//}


