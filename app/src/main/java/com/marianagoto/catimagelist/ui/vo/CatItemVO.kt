package com.marianagoto.catimagelist.ui.vo

data class CatItemVO(
    val image: CatImageVO,
    val favoriteId: Int?
) {
    val isFavorite: Boolean get() = favoriteId != null
}