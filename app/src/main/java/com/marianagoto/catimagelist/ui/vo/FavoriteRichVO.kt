package com.marianagoto.catimagelist.ui.vo

data class FavoriteRichVO(
    val favoriteId: Int,
    var isFavorite: Boolean,
    val urlImage: String,
    val breedName: String,
    val breedOrigin: String
)