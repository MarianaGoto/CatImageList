package com.marianagoto.catimagelist.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marianagoto.catimagelist.data.repository.CatRepository
import com.marianagoto.catimagelist.domain.model.CatImage
import com.marianagoto.catimagelist.ui.catlist.CatUiState
import kotlinx.coroutines.launch

class CatViewModel(
    private val repository: CatRepository
) : ViewModel() {
    private val _uiState = MutableLiveData<CatUiState>(CatUiState.Loading)
    val uiState: LiveData<CatUiState> = _uiState

    private val _cats = MutableLiveData<List<CatImage>>(emptyList())
    val cats: LiveData<List<CatImage>> = _cats

    private val _hasNoFavorites = MutableLiveData<Boolean>(false)
    val hasNoFavorites: LiveData<Boolean> = _hasNoFavorites

    private val _favorites = MutableLiveData<List<CatImage>>(emptyList())
    val favorites: LiveData<List<CatImage>> = _favorites

    init {
        loadCats()
    }

    fun loadCats() {
        viewModelScope.launch {
            _uiState.value = CatUiState.Loading

            repository.getCatsList(limit = 20)
                .onSuccess { catList ->
                    _cats.value = catList
                    _uiState.value = CatUiState.Success(catList)

                    updateFavorites(catList)

                    logCatDetails(catList)
                    updateFavoriteState()
                }.onFailure { error ->
                    val errorMessage = getErrorMessage(error)
                    Log.e("CatViewModel", "Erro ao carregar gatos: $errorMessage", error)
                    _uiState.value = CatUiState.Error(errorMessage)
                }
        }
    }

    //  * Alternar o status de favorito de um gato.
    fun toggleFavorite(cat: CatImage) {
        val currentList = _cats.value.orEmpty()
        val updatedList = currentList.map {
            if (it.id == cat.id) {
                it.copy(isFavorite = !it.isFavorite)
            } else {
                it
            }
        }
        _cats.value = updatedList
        updateFavorites(updatedList)
        updateFavoriteState()
    }

    private fun updateFavorites(list: List<CatImage>){
        val favoritesList = list.filter {it.isFavorite}
        _favorites.value = favoritesList
    }


    //    * Retorna a lista de gatos favoritos.
    fun getFavorites(): List<CatImage> {
        return _cats.value?.filter { it.isFavorite } ?: emptyList()
    }

    //  * Atualiza o estado _hasNoFavorites com base na lista atual.
    private fun updateFavoriteState() {
        val hasNoFavorites = _cats.value?.none { it.isFavorite } ?: true
        _hasNoFavorites.value = hasNoFavorites
    }

    //  * Loga os detalhes dos gatos para debug.
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

    //   * Converte exceções em mensagens de erro amigáveis.
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