package com.marianagoto.catimagelist.domain.model

import com.google.gson.annotations.SerializedName

data class CatImage(
    val id: String,
    val url: String,
    val width: Int? = null,
    val height: Int? = null,
    var isFavorite: Boolean = false,
    val breeds: List<Breed>? = null,
    val favoriteId: Int? = null
)

data class Breed(
    val id: String,
    val name: String,
    val origin: String
)

data class FavoriteRequest(
    @SerializedName("image_id") val imageId: String,
    @SerializedName("sub_id") val subId: String
)


data class FavoriteResponseAdd(
    val message: String,
    val id: Int
)

data class FavoriteResponseList(
    @SerializedName("id") val id: Int,
    @SerializedName("image_id") val imageId: String,
    @SerializedName("sub_id") val subId: String?,
    @SerializedName("image") val image: FavoriteImageInfo
)

data class FavoriteImageInfo(
@SerializedName("id") val id: String,
@SerializedName("url") val url: String
)