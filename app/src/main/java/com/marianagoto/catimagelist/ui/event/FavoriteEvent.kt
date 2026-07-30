package com.marianagoto.catimagelist.ui.event
sealed interface FavoriteEvent {
    val breedName: String

    data class Added(
        override val breedName: String
    ) : FavoriteEvent

    data class Removed(
        override val breedName: String
    ) : FavoriteEvent
}