package com.marianagoto.catimagelist.di
import com.marianagoto.catimagelist.data.repository.CatRepository
import com.marianagoto.catimagelist.ui.screens.favorites.FavoritesViewModel
import com.marianagoto.catimagelist.ui.screens.home.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module



val appModule = module {
    // Define como criar o Repository (single = Singleton, uma única instância para o app todo)
    single { CatRepository() }

    // Define como criar o ViewModel
    // O get() automaticamente busca o CatRepository que definimos acima
    viewModel { HomeViewModel(get()) }
    viewModel { FavoritesViewModel(get()) }
}