package com.marianagoto.catimagelist.ui.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marianagoto.catimagelist.BuildConfig
import com.marianagoto.catimagelist.data.dto.FavoriteRequest
import com.marianagoto.catimagelist.data.repository.CatRepository
import com.marianagoto.catimagelist.domain.mapper.catImageDTOToVO
import com.marianagoto.catimagelist.ui.helpers.UIState
import com.marianagoto.catimagelist.ui.vo.CatImageVO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch


class HomeViewModel(private val repository: CatRepository) : ViewModel() {
    private val _cats = MutableStateFlow<UIState<List<CatImageVO>>>(UIState.Loading)
    val cats: StateFlow<UIState<List<CatImageVO>>> = _cats.asStateFlow()


    private val _favoriteBreedName = MutableSharedFlow<String>(0)
    val favoriteBreedName: SharedFlow<String> = _favoriteBreedName.asSharedFlow()


    fun getCats() {
        viewModelScope.launch {
            repository.getCatsList(limit = 20)
                .onStart {
                    _cats.value = UIState.Loading
                }
                .catch { error ->
                    val errorMessage = getErrorMessage(error)
                    Log.e("HomeViewModel", "Erro ao carregar gatos: $errorMessage", error)
                    _cats.value = UIState.Error
                }
                .collect { detailedCats ->
                    val catListVO = catImageDTOToVO(detailedCats)
                    _cats.value = UIState.Success(catListVO)
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
                    _favoriteBreedName.emit(cat.breeds.firstOrNull()?.name ?: "Sem raça definida")
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