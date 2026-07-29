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
import com.marianagoto.catimagelist.ui.state.FavoriteEvent
import com.marianagoto.catimagelist.ui.state.UIState
import com.marianagoto.catimagelist.ui.vo.FavoriteRichVO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class FavoritesViewModel(private val repository: CatRepository, private val getFavoriteCatsUseCase: GetFavoriteCatsUseCase) : ViewModel(){
    private val _cats = MutableStateFlow<UIState<List<FavoriteRichVO>>>(UIState.Loading)
    val cats: StateFlow<UIState<List<FavoriteRichVO>>> = _cats.asStateFlow()

    private val _snackbarMessage = MutableLiveData<String>()
    val snackbarMessage: LiveData<String> = _snackbarMessage

    private val _favoritesEvent = MutableSharedFlow<FavoriteEvent>(0)
    val favoritesEvent: SharedFlow<FavoriteEvent> = _favoritesEvent.asSharedFlow()


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
        }
    }

    fun removeFavoriteCat(favoriteId: Int, breedName: String) {
        viewModelScope.launch {
            repository.removeFavoriteCat(favoriteId = favoriteId, apiKey = BuildConfig.API_KEY)
                .catch { error ->
                    val msg = error.localizedMessage ?: "Erro ao remover"
                    Log.e("FavoritesViewModel", "Falha ao remover favorito: $msg")

                    _favoritesEvent.emit(
                        FavoriteEvent.Error("Não foi possível remover dos favoritos")
                    )
                }
                .collect {
                    Log.d("FavoritesViewModel", "Deletado com sucesso ID: $favoriteId")

                    _favoritesEvent.emit(
                        FavoriteEvent.Success("$breedName removido dos favoritos 😢")
                    )

                    getFavoriteCats()
                }
        }
    }
}