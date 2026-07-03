package com.marianagoto.catimagelist.ui.vo

data class CatFeedItem(
    val image: CatImageVO,
    val favouriteId: Int?
) {
    val isFavourite: Boolean get() = favouriteId != null
}