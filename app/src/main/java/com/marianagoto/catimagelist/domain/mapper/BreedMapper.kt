package com.marianagoto.catimagelist.domain.mapper

import com.marianagoto.catimagelist.data.dto.BreedDto
import com.marianagoto.catimagelist.ui.vo.BreedVO

fun breedDTOToVO(breedDto: BreedDto): BreedVO{
    return BreedVO(name = breedDto.name, origin = breedDto.origin ?: "")
}

fun breedDTOToVO(breedDtoList: List<BreedDto>): List<BreedVO>{
    return breedDtoList.map { item -> breedDTOToVO(item) }
}



fun breedVOToDTO(breedVO: BreedVO): BreedDto{
    return BreedDto(
        id = "",
        name = breedVO.name,
        origin = breedVO.origin,
        weight = null,
        height = null,
        lifeSpan = "",
        breedGroup = ""
    )
}

fun breedVOToDTO(breedVOList: List<BreedVO>): List<BreedDto>{
    return breedVOList.map { item -> breedVOToDTO(item) }
}

