package com.marianagoto.catimagelist.domain.mapper

import com.marianagoto.catimagelist.data.dto.FavoriteDto
import com.marianagoto.catimagelist.data.dto.FavoriteRichResponse
import com.marianagoto.catimagelist.data.dto.ImageDto
import com.marianagoto.catimagelist.ui.vo.FavoriteRichVO

fun catImageResponseAndFavoriteResponseToFavoriteRichResponse(catImageResponse: ImageDto, favoriteResponse: FavoriteDto): FavoriteRichResponse {
    return FavoriteRichResponse(
        favoriteId = favoriteResponse.id,
        urlImage = favoriteResponse.image.url,
        breedName = catImageResponse.breeds?.firstOrNull()?.name
            ?: "Sem raça definida",
        breedOrigin = catImageResponse.breeds?.firstOrNull()?.origin
            ?: "Sem origem definida"
    )
}

fun imageDtoAndFavoriteDtoToFavoriteRichVO(imageDto: ImageDto, favoriteDto: FavoriteDto): FavoriteRichResponse {
    return FavoriteRichResponse(
        favoriteId = favoriteDto.id,
        urlImage = favoriteDto.image.url,
        breedName = imageDto.breeds.firstOrNull()?.name
            ?: "Sem raça definida",
        breedOrigin = imageDto.breeds.firstOrNull()?.origin
            ?: "Sem origem definida"
    )
}

fun favoriteRichResponseToFavoriteRichVO(favoriteRichResponse: FavoriteRichResponse): FavoriteRichVO {
    return FavoriteRichVO(
        favoriteId = favoriteRichResponse.favoriteId,
        urlImage = favoriteRichResponse.urlImage,
        breedName = favoriteRichResponse.breedName,
        breedOrigin = favoriteRichResponse.breedOrigin,
        isFavorite = true
    )
}
fun favoriteRichResponseToFavoriteRichVO(favoriteRichResponseList: List<FavoriteRichResponse>): List<FavoriteRichVO> {
    return favoriteRichResponseList.map { item -> favoriteRichResponseToFavoriteRichVO(item) }
}

