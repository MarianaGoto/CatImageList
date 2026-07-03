package com.marianagoto.catimagelist.data.dto

import com.google.gson.annotations.SerializedName

data class CreateFavouriteResponseDto(
    @SerializedName("id") val id: Int,
    @SerializedName("message") val message: String
)