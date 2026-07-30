package com.marianagoto.catimagelist.ui.event

data class FavoriteSnackbarContent(
    val breedName: String,
    val message: String,
    val icon: String
)

fun FavoriteEvent.toSnackbarContent(): FavoriteSnackbarContent {
    return when (this) {
        is FavoriteEvent.Added -> FavoriteSnackbarContent(
            breedName = breedName,
            message = "adicionado aos favoritos",
            icon = "😻"
        )

        is FavoriteEvent.Removed -> FavoriteSnackbarContent(
            breedName = breedName,
            message = "removido dos favoritos",
            icon = "😿"
        )
    }
}