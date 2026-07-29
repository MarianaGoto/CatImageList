package com.marianagoto.catimagelist.ui.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marianagoto.catimagelist.BuildConfig
import com.marianagoto.catimagelist.data.repository.CatRepository
import com.marianagoto.catimagelist.domain.mapper.catImageDTOToVO
import com.marianagoto.catimagelist.domain.mapper.catImageVOToCatItemVO
import com.marianagoto.catimagelist.domain.mapper.catImageVOToDTO
import com.marianagoto.catimagelist.ui.state.FavoriteEvent
import com.marianagoto.catimagelist.ui.state.FavoriteUpdateEvent
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
    private val _cats = MutableStateFlow<UIState<List<CatItemVO>>>(UIState.Loading)
    val cats: StateFlow<UIState<List<CatItemVO>>> = _cats.asStateFlow()

    private val _favoriteBreedName = MutableSharedFlow<String>(0)
    val favoriteBreedName: SharedFlow<String> = _favoriteBreedName.asSharedFlow()

    private val _favoriteEvent = MutableSharedFlow<FavoriteEvent>(0)
    val favoriteEvent: SharedFlow<FavoriteEvent> = _favoriteEvent.asSharedFlow()

    private val _favoriteUpdateEvent = MutableSharedFlow<FavoriteUpdateEvent>(0)
    val favoriteUpdateEvent: SharedFlow<FavoriteUpdateEvent> = _favoriteUpdateEvent.asSharedFlow()

    private var favoritesByImageId: Map<String, Int> = emptyMap()


    fun getCats() {
        viewModelScope.launch {
            repository.getFavoriteCatsList(apiKey = BuildConfig.API_KEY)
                .catch { error ->
                    Log.e("HomeViewModel", "Erro ao carregar favoritos: ${error.message}")
                    favoritesByImageId = emptyMap()
                }
                .collect { favoritesList ->
                    favoritesByImageId = favoritesList.associate { favorite ->
                        favorite.imageId to favorite.id
                }
                    Log.d("HomeViewModel", "Favoritos carregados: ${favoritesByImageId.size}")
                }







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
                    val catItemsWithFavorites = catListVO.map { catImageVO ->
                        val favoriteId = favoritesByImageId[catImageVO.id]
                        CatItemVO(
                            image = catImageVO,
                            favoriteId = favoriteId
                        )
                    }
                    _cats.value = UIState.Success(catItemsWithFavorites)
                }
        }
    }

//      * Alternar o status de favorito de um gato.
    fun addFavorite(cat: CatImageVO) {
        val catItem = catImageVOToCatItemVO(cat)
        viewModelScope.launch {
            // Converte VO para DTO (padrão do projeto)
            val imageDto = catImageVOToDTO(cat)

            // Chama repository com Flow
            repository.addFavoriteCat(imageDto, BuildConfig.API_KEY)
                .catch { error ->
                    val msg = error.localizedMessage ?: "Erro desconhecido"
                    Log.e("HomeViewModel", "Erro ao favoritar gato: $msg", error)

                    // Emite evento de erro
                    _favoriteEvent.emit(
                        FavoriteEvent.Error("Não foi possível favoritar este gato")
                    )
                }
                .collect { response ->
                    Log.d(
                        "HomeViewModel",
                        "Gato favoritado com sucesso! ID: ${response.id} | ${catItem.isFavorite}"
                    )
                    favoritesByImageId = favoritesByImageId.toMutableMap().apply {
                        put(cat.id, response.id)
                    }

                    updateCatFavoriteStatus(cat.id, response.id)

                    // Emite evento de sucesso
                    val breedName = cat.breeds.firstOrNull()?.name ?: "Gatinho fofo"
                    _favoriteEvent.emit(
                        FavoriteEvent.Success(breedName)
                    )

                    _favoriteUpdateEvent.emit(
                        FavoriteUpdateEvent.FavoriteAdded(cat.id, response.id)
                    )
                }
        }

    }


    fun removeFavorite(favoriteId: Int, imageId: String, breedName: String) {
        viewModelScope.launch {
            repository.removeFavoriteCat(favoriteId, BuildConfig.API_KEY)
                .catch { error ->
                    val msg = error.localizedMessage ?: "Erro desconhecido"
                    Log.e("HomeViewModel", "Erro ao remover favorito: $msg", error)

                    _favoriteEvent.emit(
                        FavoriteEvent.Error("Não foi possível remover dos favoritos")
                    )
                }
                .collect { response ->
                    Log.d("HomeViewModel", "Gato removido dos favoritos com sucesso! ID: $favoriteId")

                    favoritesByImageId = favoritesByImageId.toMutableMap().apply {
                        remove(imageId)
                    }
                    updateCatFavoriteStatus(imageId, null)


                    _favoriteEvent.emit(
                        FavoriteEvent.Success("$breedName removido dos favoritos")
                    )

                    _favoriteUpdateEvent.emit(
                        FavoriteUpdateEvent.FavoriteRemoved(imageId)
                    )
                }
        }
    }


    private fun updateCatFavoriteStatus(imageId: String, newFavoriteId: Int?) {
        val currentState = _cats.value
        if (currentState !is UIState.Success) return

        // Atualizar apenas o item que mudou
        val updatedCats = currentState.data.map { catItem ->
            if (catItem.image.id == imageId) {
                // ✅ Atualizar favoriteId deste gato
                catItem.copy(favoriteId = newFavoriteId)
            } else {
                catItem
            }
        }

        _cats.value = UIState.Success(updatedCats)
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

    fun toggleFavorite(item: CatItemVO) {
        Log.d("HomeViewModel", "toggleFavorite - isFavorite: ${item.isFavorite}, favoriteId: ${item.favoriteId}")
        if (item.isFavorite) {
            // Remove dos favoritos
            val breedName = item.image.breeds.firstOrNull()?.name ?: "Gatinho"
            removeFavorite(item.favoriteId!!, item.image.id, breedName)
        } else {
            // Adiciona aos favoritos
            addFavorite(item.image)
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