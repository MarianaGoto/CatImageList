package com.marianagoto.catimagelist.domain.mapper

import com.marianagoto.catimagelist.data.dto.BreedDto
import com.marianagoto.catimagelist.ui.vo.BreedVO

fun breedDTOToVO(breedDto: BreedDto): BreedVO{
    return BreedVO(name = breedDto.name, origin = breedDto.origin ?: "")
}

fun breedDTOToVO(breedDtoList: List<BreedDto>): List<BreedVO>{
    return breedDtoList.map { item -> breedDTOToVO(item) }
}
