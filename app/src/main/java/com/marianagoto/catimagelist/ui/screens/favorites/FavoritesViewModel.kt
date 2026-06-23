package com.marianagoto.catimagelist.ui.screens.favorites

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marianagoto.catimagelist.BuildConfig
import com.marianagoto.catimagelist.data.repository.CatRepository
import com.marianagoto.catimagelist.domain.model.CatImage
import com.marianagoto.catimagelist.ui.helpers.UIState
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class FavoritesViewModel(private val repository: CatRepository) : ViewModel(){
    private val _cats = MutableLiveData<UIState<List<CatImage>>>(UIState.Loading)
    val cats: LiveData<UIState<List<CatImage>>> = _cats

    private val _snackbarMessage = MutableLiveData<String>()
    val snackbarMessage: LiveData<String> = _snackbarMessage


    fun getFavoriteCats() {
        viewModelScope.launch {
            // Carregando o gato favorito - usado para erro de conexão
            _cats.value = UIState.Loading

            val result = repository.getFavoriteCatsList(apiKey = BuildConfig.API_KEY)
            result.fold(
                onSuccess = { catList ->
                    val detailedCats = catList.map{ catFav ->
                        async{
                            val detail = repository.getCatById(catFav.imageId).getOrNull()
                            detail?.copy(
                                isFavorite = true,
                                favoriteId = catFav.id
                            )
                        }
                    }.awaitAll()

                    _cats.value = UIState.Success(detailedCats.filterNotNull())
                    Log.d("CatViewModel", "gatinho listado <3")
                },
                onFailure = { error ->
                    val msg = error.localizedMessage ?: "Erro desconhecido"
                    Log.e("CatViewModel", "Erro ao listar o gato favorito: $msg", error)
                    _cats.value = UIState.Error
                }
            )
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