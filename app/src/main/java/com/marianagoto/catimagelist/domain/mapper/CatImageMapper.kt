package com.marianagoto.catimagelist.domain.mapper

import com.marianagoto.catimagelist.data.dto.ImageDto
import com.marianagoto.catimagelist.ui.vo.BreedVO
import com.marianagoto.catimagelist.ui.vo.CatImageVO

fun catImageDTOToVO(imageDto: ImageDto): CatImageVO {
    var breedsVO: List<BreedVO>

    if (imageDto.breeds == null) {
        breedsVO = emptyList()
    } else {
        breedsVO = breedDTOToVO(imageDto.breeds)
    }

    return CatImageVO(id = imageDto.id, url = imageDto.url, isFavorite = false, breeds = breedsVO)
}

fun catImageDTOToVO(imageDtoList: List<ImageDto>): List<CatImageVO> {
    return imageDtoList.map { item -> catImageDTOToVO(item) }
}
