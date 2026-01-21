package com.marianagoto.catimagelist.domain.usecase

import com.marianagoto.catimagelist.domain.model.CatImage

class ToogleFavoriteUseCase {
    operator fun invoke(cat: CatImage, currentList: List<CatImage>): List<CatImage>{
        return currentList.map{
            if (it.id == cat.id){
                it.copy(isFavorite = !it.isFavorite)
            }else{
                it
            }
        }
    }
}