package com.marianagoto.catimagelist.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marianagoto.catimagelist.data.network.RetrofitClient
import com.marianagoto.catimagelist.domain.model.CatImage
import kotlinx.coroutines.launch

class CatViewModel : ViewModel() {
    private val _cats = MutableLiveData<List<CatImage>>()
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    val cats: LiveData<List<CatImage>> = _cats

    fun loadCats() {
        viewModelScope.launch {
            if (_cats.value.isNullOrEmpty()) {
                _isLoading.value = true
                try {
                    val cats = RetrofitClient.service.searchCats(
                        limit = 20, hasBreeds = 1
                    )
                    val detailedCats = cats.map { cat ->
                        try {
                            RetrofitClient.service.searchCatById(cat.id)

                        } catch (e: Exception) {
                            cat
                        }
                    }
                    _cats.value = detailedCats
                    _isLoading.value = false
                    detailedCats.forEach { cat ->
                        Log.d(
                            "CatViewModel",
                            "Gato: ${cat.id} - Raça: ${cat.breeds?.firstOrNull()?.name} - Origem: ${cat.breeds?.firstOrNull()?.origin}"
                        )
                    }
                } catch (e: Exception) {
                    Log.e("CatViewModel", "Erro ao carregar gatos ${e.message}")
                    _isLoading.value = false
                }
            }
        }
    }

    fun setCats(list: List<CatImage>) {
        _cats.value = list
    }

    fun toggleFavorite(cat: CatImage) {
        val updatedList = _cats.value.orEmpty().map {
            if (it.id == cat.id) {
                it.copy(isFavorite = !it.isFavorite)
            } else {
                it
            }
        }
        _cats.value = updatedList
    }

    fun getFavorites(): List<CatImage> {
        return _cats.value?.filter { it.isFavorite } ?: emptyList()
    }
}