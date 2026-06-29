package com.marianagoto.catimagelist.ui.screens.favorites

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marianagoto.catimagelist.BuildConfig
import com.marianagoto.catimagelist.data.repository.CatRepository
import com.marianagoto.catimagelist.domain.mapper.favoriteRichResponseToFavoriteRichVO
import com.marianagoto.catimagelist.domain.usecase.GetFavoriteCatsUseCase
import com.marianagoto.catimagelist.ui.helpers.UIState
import com.marianagoto.catimagelist.ui.vo.FavoriteRichVO
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class FavoritesViewModel(private val repository: CatRepository, private val getFavoriteCatsUseCase: GetFavoriteCatsUseCase) : ViewModel(){
    private val _cats = MutableLiveData<UIState<List<FavoriteRichVO>>>(UIState.Loading)
    val cats: LiveData<UIState<List<FavoriteRichVO>>> = _cats

    private val _snackbarMessage = MutableLiveData<String>()
    val snackbarMessage: LiveData<String> = _snackbarMessage


    fun getFavoriteCats() {
        viewModelScope.launch {
            // Carregando o gato favorito - usado para erro de conexão
            _cats.value = UIState.Loading

            getFavoriteCatsUseCase.getFavoriteRichCats()
                .catch { error ->
                    _cats.value = UIState.Error
                    Log.e("FavoritesViewModel", "Erro: ${error.message}")
                }
                .collect { detailedList ->
                    _cats.value = UIState.Success(favoriteRichResponseToFavoriteRichVO(detailedList))
                }


//            val result = repository.getFavoriteCatsList(apiKey = BuildConfig.API_KEY)
//            result.fold(
//                onSuccess = { favoriteResponse ->
//                    val detailedCats = favoriteResponse.map { item ->
//                        val catImageResponse = repository.getCatById(item.imageId)
//                        val catImageVO = catImageDTOToVO(catImageResponse)
//                    }
//                        async{
//                            val detail = repository.getCatById(catFav.imageId)
//                            val catListVO = catImageDTOToVO(detail)
//                            catListVO.copy(
//                                isFavorite = true,
//                                favoriteId = catFav.id
//                            )
//                        }
//                    }.awaitAll()

//                    _cats.value = UIState.Success(detailedCats.filterNotNull())
//                    Log.d("CatViewModel", "gatinho listado <3")
//                },
//                onFailure = { error ->
//                    val msg = error.localizedMessage ?: "Erro desconhecido"
//                    Log.e("CatViewModel", "Erro ao listar o gato favorito: $msg", error)
//                    _cats.value = UIState.Error
//                }
//            )
        }
    }

    fun removeFavoriteCat(favouriteId: Int) {
        viewModelScope.launch {
            val result = repository.removeFavoriteCat(favouriteId = favouriteId, apiKey = BuildConfig.API_KEY)

            result.fold(
                onSuccess = { catList ->
                    Log.d("FavoritesViewModel", "Deletado com sucesso ID: $favouriteId")
                    getFavoriteCats()
                    _snackbarMessage.value = "Removido dos favoritos"

                },
                onFailure = { error ->
                    val msg = error.localizedMessage ?: "Erro ao remover"
                    Log.e("FavoritesViewModel", "Falha ao remover favorito: $msg")
                }
            )
        }
    }
}