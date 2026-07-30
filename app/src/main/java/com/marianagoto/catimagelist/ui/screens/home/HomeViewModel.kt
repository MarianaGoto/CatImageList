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
import com.marianagoto.catimagelist.ui.event.FavoriteEvent


class HomeViewModel(private val repository: CatRepository) : ViewModel() {

    private val _cats = MutableStateFlow<UIState<List<CatImageVO>>>(UIState.Loading)
    val cats: StateFlow<UIState<List<CatImageVO>>> = _cats.asStateFlow()

    private val _favoriteEvent  = MutableSharedFlow<FavoriteEvent>(0)
    val favoriteEvent : SharedFlow<FavoriteEvent> = _favoriteEvent .asSharedFlow()


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

    private val favoriteRequestsInFlight = mutableSetOf<String>()

    fun toggleFavorite(cat: CatImageVO, subId: String) {
        if (!favoriteRequestsInFlight.add(cat.id)) return

        viewModelScope.launch {
            updateCat(cat.id) { it.copy(isLoadingFavorite = true) }

            try {
                if (cat.isFavorite) {
                    removeFavorite(cat)
                } else {
                    addFavorite(cat, subId)
                }
            } finally {
                favoriteRequestsInFlight.remove(cat.id)
            }
        }
    }

    private suspend fun addFavorite(cat: CatImageVO, subId: String) {
        val request = FavoriteRequest(
            imageId = cat.id,
            subId = subId
        )

        repository.addFavoriteCat(
            favoriteRequest = request,
            apiKey = BuildConfig.API_KEY
        ).fold(
            onSuccess = { response ->
                updateCat(cat.id) {
                    it.copy(
                        isFavorite = true,
                        favoriteId = response.id,
                        isLoadingFavorite = false
                    )
                }

                _favoriteEvent.emit(
                    FavoriteEvent.Added(
                        cat.breeds.firstOrNull()?.name ?: "Gatinho"
                    )
                )
                Log.d("HomeViewModel", "Gatinho adicionado aos favoritos")
            },
            onFailure = {
                updateCat(cat.id) { current ->
                    current.copy(isLoadingFavorite = false)
                }
            }
        )
    }

    private suspend fun removeFavorite(cat: CatImageVO) {
        val favoriteId = cat.favoriteId

        if (favoriteId == null) {
            updateCat(cat.id) { current ->
                current.copy(isLoadingFavorite = false)
            }
            return
        }

        repository.removeFavoriteCat(
            favouriteId = favoriteId,
            apiKey = BuildConfig.API_KEY
        ).fold(
            onSuccess = {
                updateCat(cat.id) {
                    it.copy(
                        isFavorite = false,
                        favoriteId = null,
                        isLoadingFavorite = false
                    )
                }
                _favoriteEvent.emit(
                    FavoriteEvent.Removed(
                        cat.breeds.firstOrNull()?.name ?: "Gatinho"
                    )
                )
                Log.d("HomeViewModel", "Gatinho removido dos favoritos")
            },
            onFailure = {
                updateCat(cat.id) { current ->
                    current.copy(isLoadingFavorite = false)
                }
            }
        )
    }

    private fun updateCat(
        catId: String,
        transform: (CatImageVO) -> CatImageVO
    ) {
        val currentList = (_cats.value as? UIState.Success)?.data ?: return

        _cats.value = UIState.Success(
            currentList.map { cat ->
                if (cat.id == catId) transform(cat) else cat
            }
        )
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