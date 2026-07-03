package com.marianagoto.catimagelist.data.dto

import com.google.gson.annotations.SerializedName

data class CatImageResponse (
    val id: String,
    val url: String,
    val width: Int,
    val height: Int,
    @SerializedName("mime_type") val mimeType: String,
    val breeds: List<BreedDto>?
)