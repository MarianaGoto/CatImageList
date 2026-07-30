package com.marianagoto.catimagelist.ui.vo

data class CatImageVO(
    val id: String,
    val url: String,
    var isFavorite: Boolean,
    val breeds: List<BreedVO>,
    val favoriteId: Int?,
    val isLoadingFavorite: Boolean = false
)