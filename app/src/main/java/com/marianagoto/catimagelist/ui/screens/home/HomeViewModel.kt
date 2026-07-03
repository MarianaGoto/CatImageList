package com.marianagoto.catimagelist.ui.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marianagoto.catimagelist.BuildConfig
import com.marianagoto.catimagelist.data.repository.CatRepository
import com.marianagoto.catimagelist.domain.mapper.catImageDTOToVO
import com.marianagoto.catimagelist.domain.mapper.catImageVOToDTO
import com.marianagoto.catimagelist.ui.state.UIState
import com.marianagoto.catimagelist.ui.vo.CatItemVO
import com.marianagoto.catimagelist.ui.vo.CatImageVO
import com.marianagoto.catimagelist.ui.vo.FavoriteRichVO
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
    fun addFavorite(cat: CatImageVO, subId: String) {

    }


    fun removeFavoriteCat(cat: CatImageVO, favoriteCat: FavoriteRichVO) {
        viewModelScope.launch {
            repository.removeFavoriteCat(favoriteId = favoriteCat.favoriteId, apiKey = BuildConfig.API_KEY)
                .catch { error ->
                    val msg = error.localizedMessage ?: "Erro ao remover"
                    Log.e("FavoritesViewModel", "Falha ao remover favorito: ${favoriteCat.favoriteId}")
                }
                .collect { response ->
                    Log.d("FavoritesViewModel", "Deletado com sucesso ID: ${favoriteCat.favoriteId}.")
                }
        }
    }

    fun getFavoritesById(favoriteCat: Int){
        viewModelScope.launch {
            repository.getFavoriteCatById(favoriteId = favoriteCat, apiKey = BuildConfig.API_KEY)
                .catch { error ->
                    val msg = error.localizedMessage ?: "Erro ao localizar o gato por id"
                    Log.e("HomeViewModel", "Falha ao Erro ao localizar o gato por id: $msg")
                }
                .collect {
                    Log.d("HomeViewModel", "Localizado com sucesso ID: ${favoriteCat}")
                }
        }
    }

    /** Chamado pelo tap no ícone de coração de um card na Home. */
    fun onToggleFavorite(item: CatItemVO) {
        viewModelScope.launch {
            if (item.favoriteId != null) {
                repository.removeFavoriteCat(item.favoriteId, BuildConfig.API_KEY)
                    .catch { error -> }
                    .collect {}
            } else {
                val catVO = catImageVOToDTO(item.image)
                repository.addFavoriteCat(catVO, BuildConfig.API_KEY)
                    .catch { error -> }
                    .collect {
                        Log.d("HomeViewModel", "gatinho favoritado!")
//                        _favoriteBreedName.emit(cat.breeds.firstOrNull()?.name ?: "Sem raça definida") }

                    }
            }
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