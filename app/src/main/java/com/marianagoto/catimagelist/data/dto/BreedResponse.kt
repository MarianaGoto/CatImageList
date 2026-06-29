package com.marianagoto.catimagelist.data.dto

import com.google.gson.annotations.SerializedName

data class BreedResponse(
    val id: String,
    val name: String,
    val weight: Weight?,
    val height: String?,
    @SerializedName("life_span") val lifeSpan: String,
    @SerializedName("breed_group") val breedGroup: String,
    val origin: String?
)

data class Weight(
    val imperial: String,
    val metric: String
)