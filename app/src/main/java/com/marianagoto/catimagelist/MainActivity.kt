package com.marianagoto.catimagelist

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.marianagoto.catimagelist.databinding.ActivityMainBinding
import com.marianagoto.catimagelist.ui.util.AnimationUtils
import com.marianagoto.catimagelist.ui.screens.favorites.FavoritesFragment
import com.marianagoto.catimagelist.ui.screens.home.HomeFragment


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.main

        if (savedInstanceState == null) {
            openFragment(HomeFragment())
        }


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
                    openFragment(FavoritesFragment())
                    true
                }

                else -> false
            }
        }
    }

    private fun animateIconOnly(menuItemId: Int) {
        val menuItemView = binding.bottomNavigation.findViewById<View>(menuItemId)

        if (menuItemView is BottomNavigationItemView) {
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

