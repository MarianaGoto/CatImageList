package com.marianagoto.catimagelist.ui.catlist

import com.marianagoto.catimagelist.domain.model.CatImage
import com.marianagoto.catimagelist.domain.model.FavoriteResponseList

sealed class CatUiState {
    object Loading : CatUiState()
    object Favoritado : CatUiState()
    data class Success(val cats: List<CatImage>) : CatUiState()
    data class SucessoFavoritado(val favoritesCats: List<FavoriteResponseList>) : CatUiState()
    data class Error(val message: String) : CatUiState()
}