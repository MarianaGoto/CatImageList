package com.marianagoto.catimagelist.domain.model

data class CatImage(
    val id: String,
    val url: String,
    val width: Int? = null,
    val height: Int? = null,
    var isFavorite: Boolean = false,
    val breeds: List<Breed>? = null
)

data class Breed(
    val id: String,
    val name: String,
    val origin: String
)