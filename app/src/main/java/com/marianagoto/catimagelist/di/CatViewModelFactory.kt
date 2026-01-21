package com.marianagoto.catimagelist.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.marianagoto.catimagelist.data.repository.CatRepository
import com.marianagoto.catimagelist.ui.viewmodel.CatViewModel

class CatViewModelFactory: ViewModelProvider.Factory {

@Suppress("UNCHECKED_CAST")
override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(CatViewModel::class.java)){
        // Criar o repository
        val repository = CatRepository()

        // Injetar no ViewModel
        return CatViewModel(
            repository = repository
        ) as T
    }
    throw IllegalArgumentException("ViewModel desconhecido: ${modelClass.name}")

}
}