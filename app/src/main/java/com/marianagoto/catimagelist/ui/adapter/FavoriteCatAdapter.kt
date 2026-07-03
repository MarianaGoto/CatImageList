package com.marianagoto.catimagelist.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.ddd.androidutils.DoubleClick
import com.ddd.androidutils.DoubleClickListener
import com.marianagoto.catimagelist.R
import com.marianagoto.catimagelist.databinding.ItemCatBinding
import com.marianagoto.catimagelist.ui.vo.FavoriteRichVO
import com.marianagoto.catimagelist.ui.util.AnimationUtils


class FavoriteCatAdapter(
    private val onFavoriteClick: (FavoriteRichVO) -> Unit
) : ListAdapter<FavoriteRichVO, FavoriteCatAdapter.VH>(DIFF_CALLBACK) {
    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<FavoriteRichVO>() {
            override fun areItemsTheSame(old: FavoriteRichVO, new: FavoriteRichVO) = old.favoriteId == new.favoriteId

            override fun areContentsTheSame(old: FavoriteRichVO, new: FavoriteRichVO) = old == new
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemCatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val cat = getItem(position)
        holder.bind(cat, onFavoriteClick)
    }

    class VH(val binding: ItemCatBinding) : RecyclerView.ViewHolder(binding.root) {
        private var currentCat: FavoriteRichVO? = null
        private var currentOnFavoriteClick: ((FavoriteRichVO) -> Unit)? = null
        private var lastClickTime: Long = 0
        val doubleClick = DoubleClick(object : DoubleClickListener {
            override fun onSingleClickEvent(view: View?) {
                //
            }

            override fun onDoubleClickEvent(view: View?)  {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastClickTime < 500) return
                lastClickTime = currentTime

                currentCat?.let { cat ->
                    AnimationUtils.animatePop(binding.ivFavoriteIcon)
                    cat.isFavorite = !cat.isFavorite
                    updateFavoriteUI(cat)
                    currentOnFavoriteClick?.invoke(cat)
                }

            }
        })

        fun bind(cat: FavoriteRichVO, onFavoriteClick: (FavoriteRichVO) -> Unit) = with(binding) {
            currentCat = cat
            currentOnFavoriteClick = onFavoriteClick
            sivCat.load(cat.urlImage)

            val breed = cat.breedName.firstOrNull()

            tvTitleImg.text = cat.breedName
            tvSubtitleImg.text = cat.breedOrigin
            updateFavoriteUI(cat)

            ivFavoriteIcon.setOnClickListener { view ->
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastClickTime < 500) return@setOnClickListener
                lastClickTime = currentTime

                AnimationUtils.animatePop(view)
                cat.isFavorite = !cat.isFavorite
                updateFavoriteUI(cat)
                onFavoriteClick(cat)
            }
            sivCat.setOnClickListener(doubleClick)
        }
        private fun updateFavoriteUI(cat: FavoriteRichVO) {
            if (cat.isFavorite) {
                binding.ivFavoriteIcon.setColorFilter(
                    ContextCompat.getColor(binding.root.context, R.color.red_300)
                )
                binding.ivCircleShape.setColorFilter(Color.WHITE)
            } else {
                binding.ivFavoriteIcon.clearColorFilter()
                binding.ivCircleShape.clearColorFilter()
            }
        }
    }
}
