package com.marianagoto.catimagelist.data.dto

import com.google.gson.annotations.SerializedName

data class FavoriteDto (
    @SerializedName("id") val id: Int,
    @SerializedName("user_id") val userId: String? = null,
    @SerializedName("image_id") val imageId: String,
    @SerializedName("sub_id") val subId: String? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("image") val image: ImageDto
)