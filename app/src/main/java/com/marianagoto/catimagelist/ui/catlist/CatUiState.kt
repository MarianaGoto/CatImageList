package com.marianagoto.catimagelist.ui.catlist

import com.marianagoto.catimagelist.domain.model.CatImage

sealed class CatUiState {
    object Loading : CatUiState()
    data class Success(val cats: List<CatImage>) : CatUiState()
    data class Error(val message: String) : CatUiState()
}