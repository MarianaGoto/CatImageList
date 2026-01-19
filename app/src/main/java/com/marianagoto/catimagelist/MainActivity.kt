package com.marianagoto.catimagelist

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.marianagoto.catimagelist.databinding.ActivityMainBinding
import com.marianagoto.catimagelist.ui.viewmodel.CatViewModel
import com.marianagoto.catimagelist.util.AnimationUtils


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: CatViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.main

//        if (savedInstanceState == null) {
//            openFragment(LoadingFragment())
//            openFragment(HomeFragment())
//
//        }

        viewModel.isLoading.observe(this){ isLoading ->
            if (isLoading){
                openFragment(LoadingFragment())
            }else{
                openFragment(HomeFragment())
            }
        }
        viewModel.loadCats()
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    Log.d("Animation", "Clicou em Home - ID: ${item.itemId}")
                    animateIconOnly(R.id.menu_home)
                    openFragment(HomeFragment())
                    true
                }

                R.id.menu_favorites -> {
                    Log.d("Animation", "Clicou em Favoritos - ID: ${item.itemId}")
                    animateIconOnly(R.id.menu_favorites)
                    val favorites = viewModel.getFavorites()
                    if (favorites.isEmpty()){
                        openFragment(EmptyFavoritesFragment())
                    }else{
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
                // Usa animação de SCALE em vez de TRANSLATION
                AnimationUtils.animateBounceScale(iconView)
            }
        }
    }

    private fun openFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment).commit()
    }

}

