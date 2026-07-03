package com.marianagoto.catimagelist.data.dto

import com.google.gson.annotations.SerializedName

data class CreateFavoriteResponseDto(
    @SerializedName("id") val id: Int,
    @SerializedName("message") val message: String
)