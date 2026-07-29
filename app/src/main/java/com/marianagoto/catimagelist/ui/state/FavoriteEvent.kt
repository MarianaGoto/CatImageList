package com.marianagoto.catimagelist.ui.state

/**
 * Eventos relacionados a operações de favoritos
 * Usados para feedback visual (SnackBar, Toast, etc)
 */
sealed class FavoriteEvent {
    data class Success(val message: String) : FavoriteEvent()
    data class Error(val message: String) : FavoriteEvent()
}

sealed class FavoriteUpdateEvent {
    data class FavoriteAdded(val imageId: String, val favoriteId: Int) : FavoriteUpdateEvent()
    data class FavoriteRemoved(val imageId: String) : FavoriteUpdateEvent()
}