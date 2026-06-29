package com.marianagoto.catimagelist.data.dto

import com.google.gson.annotations.SerializedName

data class FavoriteRequest(
    @SerializedName("image_id") val imageId: String,
    @SerializedName("sub_id") val subId: String
)
