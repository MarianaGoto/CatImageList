package com.marianagoto.catimagelist.domain.mapper

import com.marianagoto.catimagelist.data.dto.BreedResponse
import com.marianagoto.catimagelist.ui.vo.BreedVO

fun breedDTOToVO(breedResponse: BreedResponse): BreedVO{
    return BreedVO(name = breedResponse.name, origin = breedResponse.origin ?: "")
}

fun breedDTOToVO(breedResponseList: List<BreedResponse>): List<BreedVO>{
    return breedResponseList.map { item -> breedDTOToVO(item) }
}
