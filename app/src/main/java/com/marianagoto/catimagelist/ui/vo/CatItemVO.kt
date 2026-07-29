package com.marianagoto.catimagelist.ui.vo

data class CatItemVO(
    val image: CatImageVO,
    val favoriteId: Int?,
    var isFavorite: Boolean = false
) {
//    var isFavorite: Boolean get() = favoriteId != null
}