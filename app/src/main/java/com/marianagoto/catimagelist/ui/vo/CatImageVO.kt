package com.marianagoto.catimagelist.ui.vo

data class CatImageVO(
    val id: String,
    val url: String,
    var isFavorite: Boolean, //deletar
    val breeds: List<BreedVO>
)