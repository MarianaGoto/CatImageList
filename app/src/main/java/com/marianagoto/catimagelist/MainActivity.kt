package com.marianagoto.catimagelist

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.marianagoto.catimagelist.databinding.ActivityMainBinding
import com.marianagoto.catimagelist.di.CatViewModelFactory
import com.marianagoto.catimagelist.ui.catlist.CatUiState
import com.marianagoto.catimagelist.ui.viewmodel.CatViewModel
import com.marianagoto.catimagelist.util.AnimationUtils
import com.marianagoto.catimagelist.ErrorFragment


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: CatViewModel by viewModels { CatViewModelFactory() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.main

        viewModel.uiState.observe(this) { state ->
            when (state) {
                is CatUiState.Loading -> {
                    // Só abre LoadingFragment se NÃO for atualização de favorito
                    val isNotManualUpdate = true
                    if (isNotManualUpdate) {
                        openFragment(LoadingFragment())
                    }
                }

                is CatUiState.Success -> {
                    // Só abre HomeFragment se veio de Loading/Error/Não estiver em outro fragment
                    val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
                    if (currentFragment !is HomeFragment &&
                        currentFragment !is FavoritesFragment &&
                        currentFragment !is EmptyFavoritesFragment &&
                        currentFragment !is ErrorFragment) {
                        openFragment(HomeFragment())
                    }
                }

                is CatUiState.Error -> {
                    Log.e("MainActivity", "Erro ao carregar gatos: ${state.message}")
                    openFragment(ErrorFragment())
                }
            }
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    Log.d("Animation", "Clicou em Home - ID: ${item.itemId}")
                    animateIconOnly(R.id.menu_home)
                    if (viewModel.uiState.value is CatUiState.Error) {
                        viewModel.loadCats()
                    }
                    openFragment(HomeFragment())
                    true
                }

                R.id.menu_favorites -> {
                    Log.d("Animation", "Clicou em Favoritos - ID: ${item.itemId}")
                    animateIconOnly(R.id.menu_favorites)
                    val favorites = viewModel.getFavorites()
                    if (favorites.isEmpty()) {
                        openFragment(EmptyFavoritesFragment())
                    } else {
                        openFragment(FavoritesFragment())
                    }
                    true
                }

                else -> false
            }
        }
    }

    private fun animateIconOnly(menuItemId: Int) {
        val menuItemView = binding.bottomNavigation.findViewById<View>(menuItemId)

        if (menuItemView is com.google.android.material.bottomnavigation.BottomNavigationItemView) {
            var iconView: View? = null

            if (menuItemView is android.view.ViewGroup) {
                for (i in 0 until menuItemView.childCount) {
                    val child = menuItemView.getChildAt(i)

                    if (child is android.view.ViewGroup) {
                        for (j in 0 until child.childCount) {
                            val subChild = child.getChildAt(j)
                            if (subChild is android.widget.ImageView) {
                                iconView = subChild
                                break
                            }
                        }
                    }
                    if (iconView != null) break
                }
            }

            if (iconView != null) {
                AnimationUtils.animateBounceScale(iconView)
            }
        }
    }

    fun openFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment).commit()
    }
}

