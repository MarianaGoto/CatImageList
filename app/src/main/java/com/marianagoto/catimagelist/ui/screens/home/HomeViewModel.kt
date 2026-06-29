package com.marianagoto.catimagelist.ui.screens.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marianagoto.catimagelist.BuildConfig
import com.marianagoto.catimagelist.data.dto.FavoriteRequest
import com.marianagoto.catimagelist.data.repository.CatRepository
import com.marianagoto.catimagelist.domain.mapper.catImageDTOToVO
import com.marianagoto.catimagelist.ui.helpers.UIState
import com.marianagoto.catimagelist.ui.vo.CatImageVO
import kotlinx.coroutines.launch


class HomeViewModel(private val repository: CatRepository) : ViewModel() {
    private val _cats = MutableLiveData<UIState<List<CatImageVO>>>()
    val cats: LiveData<UIState<List<CatImageVO>>> = _cats

    private val _favoriteBreedName = MutableLiveData<String>()
    val favoriteBreedName: LiveData<String> = _favoriteBreedName



    fun getCats() {
        viewModelScope.launch {
            // Carregando lista de gatos - usado para erro de conexão
            _cats.value = UIState.Loading

            repository.getCatsList(limit = 20)
                .onSuccess { catListResponse ->
                    val catListVO = catImageDTOToVO(catListResponse)
                    _cats.value = UIState.Success(catListVO)
                    logCatDetails(catListVO)

                }.onFailure { error ->
                    val errorMessage = getErrorMessage(error)
                    Log.e("CatViewModel", "Erro ao carregar gatos: $errorMessage", error)
                    _cats.value = UIState.Error
                }
        }
    }

//      * Alternar o status de favorito de um gato.
      fun toggleFavorite(cat: CatImageVO, subId: String) {

        val favoriteRequest = FavoriteRequest(imageId = cat.id, subId = subId)

        viewModelScope.launch {
            val result = repository.addFavoriteCat(
                favoriteRequest = favoriteRequest, apiKey = BuildConfig.API_KEY
            )
            result.fold(
                onSuccess = { response ->
                    Log.d("CatViewModel", "gatinho favoritado!")

//                    cat.favoriteId = response.id
//                    cat.isFavorite = true
                    _favoriteBreedName.postValue(cat.breeds.firstOrNull()?.name ?: "Sem raça definida")
                },
                onFailure = { error ->
                    val msg = error.localizedMessage ?: "Erro desconhecido"
                    Log.e("CatViewModel", "Erro ao adicionar favorito: $msg", error)

//                    cat.isFavorite = false
                    _cats.value = UIState.Error
                }
            )
        }
    }

    fun resetFavoriteBreedName() {
        _favoriteBreedName.value = null
    }

    fun removeFavoriteCat(favouriteId: Int) {
        viewModelScope.launch {
            val result = repository.removeFavoriteCat(favouriteId = favouriteId, apiKey = BuildConfig.API_KEY)

            result.fold(
                onSuccess = { catList ->
                    Log.d("FavoritesViewModel", "Deletado com sucesso ID: $favouriteId")

//                    _snackbarMessage.value = "Removido dos favoritos"

                },
                onFailure = { error ->
                    val msg = error.localizedMessage ?: "Erro ao remover"
                    Log.e("FavoritesViewModel", "Falha ao remover favorito: $msg")
                }
            )
        }
    }

    private fun logCatDetails(cats: List<CatImageVO>) {
        cats.forEach { cat ->
            val breed = cat.breeds.firstOrNull()
            val breedName = breed?.name ?: "Unknown"
            val origin = breed?.origin ?: "Unknown"

            Log.d(
                "CatViewModel", "Gato: ${cat.id} - Raça: $breedName - Origem: $origin"
            )
        }
    }

    private fun getErrorMessage(error: Throwable): String {
        return when {
            error is java.net.UnknownHostException -> "Sem conexão com a internet. Verifique sua conexão."

            error is java.net.SocketTimeoutException -> "Tempo de conexão esgotado. Tente novamente."

            error.message?.contains(
                "404",
                ignoreCase = true
            ) == true -> "Recurso não encontrado. Tente novamente."

            else -> "Não foi possível carregar as imagens. Tente novamente mais tarde."
        }
    }
}