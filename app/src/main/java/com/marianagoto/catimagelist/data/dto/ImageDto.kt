package com.marianagoto.catimagelist.data.dto

import com.google.gson.annotations.SerializedName

data class ImageDto(
    @SerializedName("id") val id: String,
    @SerializedName("url") val url: String,
    @SerializedName("width") val width: Int? = null,
    @SerializedName("height") val height: Int? = null,
    @SerializedName("mime_type") val mimeType: String? = null,
    @SerializedName("breeds") val breeds: List<BreedDto>
)
