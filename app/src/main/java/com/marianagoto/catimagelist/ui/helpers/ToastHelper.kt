package com.marianagoto.catimagelist.ui.helpers

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.marianagoto.catimagelist.R
import com.marianagoto.catimagelist.databinding.CustomToastBinding
import com.marianagoto.catimagelist.ui.vo.CatImageVO

object ToastHelper {

    fun showToast(context: Context, cat: CatImageVO) {
        val inflater = LayoutInflater.from(context)
        val binding = CustomToastBinding.inflate(inflater)

        val breedName = cat.breeds.firstOrNull()?.name ?: "Gatinho fofo"
        binding.tvBreed.text = breedName

//        binding.ivCat.load(cat.url){
//            crossfade(true)
//            placeholder(R.drawable.cat_icon)
//        }

        val toast = Toast(context)
        toast.setGravity(Gravity.BOTTOM or Gravity.FILL_HORIZONTAL, 0, 100)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = binding.root
        toast.show()

        binding.ivClose.setOnClickListener { view ->
//            toast.cancel()
            Log.d("Toast", "Toast closed ${view}")
        }
    }

    @SuppressLint("RestrictedApi")
    fun ShowCustomSnackbar(view: View, cat: CatImageVO) {
        val snackbar = Snackbar.make(view, "", Snackbar.LENGTH_SHORT)
        val inflater = LayoutInflater.from(view.context)
        val snackBinding = CustomToastBinding.inflate(inflater)
        val breedName = cat.breeds.firstOrNull()?.name ?: "Gatinho"
        snackBinding.tvBreed.text = breedName
        val snackbarLayout = snackbar.view as Snackbar.SnackbarLayout

        snackbarLayout.setBackgroundColor(android.graphics.Color.TRANSPARENT)
        snackbarLayout.setPadding(0, 0, 0, 0)
        snackbarLayout.removeAllViews()
        snackbarLayout.addView(snackBinding.root, 0)

        snackBinding.ivClose.setOnClickListener {
            snackbar.dismiss()
        }

        val activity = view.context as? AppCompatActivity
        val bottomNav = activity?.findViewById<View>(R.id.bottomNavigation)

        if (bottomNav != null) {
            snackbar.anchorView = bottomNav
        }
        snackbar.show()
    }
}