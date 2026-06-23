package com.marianagoto.catimagelist.ui.helpers

import com.marianagoto.catimagelist.ui.catlist.CatUiState

sealed class UIState<out T> {
    object Loading : UIState<Nothing>()
    object Error : UIState<Nothing>()
    data class Success<T>(val data: T) : UIState<T>()
}