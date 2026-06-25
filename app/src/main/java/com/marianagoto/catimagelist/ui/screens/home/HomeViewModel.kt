package com.marianagoto.catimagelist.ui.screens.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marianagoto.catimagelist.BuildConfig
import com.marianagoto.catimagelist.data.repository.CatRepository
import com.marianagoto.catimagelist.domain.model.CatImage
import com.marianagoto.catimagelist.domain.model.FavoriteRequest
import com.marianagoto.catimagelist.ui.helpers.UIState
import kotlinx.coroutines.launch


class HomeViewModel(private val repository: CatRepository) : ViewModel() {
    private val _cats = MutableLiveData<UIState<List<CatImage>>>(UIState.Loading)
    val cats: LiveData<UIState<List<CatImage>>> = _cats

    private val _snackbarMessage = MutableLiveData<CatImage>()
    val snackbarMessage: LiveData<CatImage> = _snackbarMessage

    fun getCats() {
        viewModelScope.launch {
            // Carregando lista de gatos - usado para erro de conexão
            _cats.value = UIState.Loading

            repository.getCatsList(limit = 20)
                .onSuccess { catList ->
                    _cats.value = UIState.Success(catList)
                    logCatDetails(catList)

                }.onFailure { error ->
                    val errorMessage = getErrorMessage(error)
                    Log.e("CatViewModel", "Erro ao carregar gatos: $errorMessage", error)
                    _cats.value = UIState.Error
                }
        }
    }

//      * Alternar o status de favorito de um gato.
      fun toggleFavorite(cat: CatImage, subId: String) {

        val favoriteRequest = FavoriteRequest(imageId = cat.id, subId = subId)

        viewModelScope.launch {
            val result = repository.addFavoriteCat(
                favoriteRequest = favoriteRequest, apiKey = BuildConfig.API_KEY
            )
            result.fold(
                onSuccess = {
                    Log.d("CatViewModel", "gatinho favoritado!")
                    _snackbarMessage.value = cat
                },
                onFailure = { error ->
                    val msg = error.localizedMessage ?: "Erro desconhecido"
                    Log.e("CatViewModel", "Erro ao adicionar favorito: $msg", error)
                    _cats.value = UIState.Error
                }
            )
        }
    }

    private fun logCatDetails(cats: List<CatImage>) {
        cats.forEach { cat ->
            val breed = cat.breeds?.firstOrNull()
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