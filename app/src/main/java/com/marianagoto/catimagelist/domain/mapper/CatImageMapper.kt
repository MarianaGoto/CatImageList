package com.marianagoto.catimagelist.domain.mapper

import com.marianagoto.catimagelist.data.dto.CatImageResponse
import com.marianagoto.catimagelist.ui.vo.BreedVO
import com.marianagoto.catimagelist.ui.vo.CatImageVO

fun catImageDTOToVO(catImageResponse: CatImageResponse): CatImageVO {
    var breedsVO: List<BreedVO>

    if (catImageResponse.breeds == null) {
        breedsVO = emptyList()
    } else {
        breedsVO = breedDTOToVO(catImageResponse.breeds)
    }

    return CatImageVO(id = catImageResponse.id, url = catImageResponse.url, isFavorite = false, breeds = breedsVO)
}

fun catImageDTOToVO(catImageResponseList: List<CatImageResponse>): List<CatImageVO> {
    return catImageResponseList.map { item -> catImageDTOToVO(item) }
}
