package com.marianagoto.catimagelist.data.dto

import com.google.gson.annotations.SerializedName

data class FavoriteResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("user_id") val userID: String,
    @SerializedName("image_id") val imageId: String,
    @SerializedName("sub_id") val subId: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("image") val image: FavoriteImageInfo
)
