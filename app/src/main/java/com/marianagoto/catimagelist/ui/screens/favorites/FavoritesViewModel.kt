package com.marianagoto.catimagelist.ui.screens.favorites

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marianagoto.catimagelist.BuildConfig
import com.marianagoto.catimagelist.data.repository.CatRepository
import com.marianagoto.catimagelist.domain.model.CatImage
import com.marianagoto.catimagelist.domain.model.FavoriteResponseList
import com.marianagoto.catimagelist.ui.helpers.UIState
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class FavoritesViewModel(private val repository: CatRepository) : ViewModel(){
    private val _cats = MutableLiveData<UIState<List<CatImage>>>(UIState.Loading)
    val cats: LiveData<UIState<List<CatImage>>> = _cats

    fun getFavoriteCats() {
        viewModelScope.launch {

            val result = repository.getFavoriteCatsList(apiKey = BuildConfig.API_KEY)
            result.fold(
                onSuccess = { catList ->
                    /*teste datalhes gato favorito*/
                    val detailedCats = catList.map{ catFav ->
                        async{
                            repository.getCatById(catFav.imageId).getOrNull()
                        }
                    }.awaitAll()
                    val finalList = detailedCats.filterNotNull().map{
                        it.copy(isFavorite = true)
                    }

                    _cats.value = UIState.Success(finalList)
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
}