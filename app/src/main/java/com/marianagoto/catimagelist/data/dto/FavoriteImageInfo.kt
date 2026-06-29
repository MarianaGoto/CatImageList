package com.marianagoto.catimagelist.data.dto

import com.google.gson.annotations.SerializedName

data class FavoriteImageInfo(
    @SerializedName("id") val id: String,
    @SerializedName("url") val url: String
)
