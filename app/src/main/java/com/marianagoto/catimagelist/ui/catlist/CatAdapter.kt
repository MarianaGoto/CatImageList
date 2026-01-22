package com.marianagoto.catimagelist.ui.catlist

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
import com.marianagoto.catimagelist.domain.model.CatImage
import com.marianagoto.catimagelist.util.AnimationUtils


class CatAdapter(
    private val onFavoriteClick: (CatImage) -> Unit
) : ListAdapter<CatImage, CatAdapter.VH>(DIFF_CALLBACK) {
    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CatImage>() {
            override fun areItemsTheSame(old: CatImage, new: CatImage) = old.id == new.id

            override fun areContentsTheSame(old: CatImage, new: CatImage) = old == new
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
        private var currentCat: CatImage? = null
        private var currentOnFavoriteClick: ((CatImage) -> Unit)? = null
        val doubleClick = DoubleClick(object : DoubleClickListener {
            override fun onSingleClickEvent(view: View?) {
                //
            }

            override fun onDoubleClickEvent(view: View?)  {
                currentCat?.let { cat ->
                    AnimationUtils.animatePop(binding.ivFavoriteIcon)
                    currentOnFavoriteClick?.invoke(cat)
                }

            }
        })

        fun bind(cat: CatImage, onFavoriteClick: (CatImage) -> Unit) = with(binding) {
            currentCat = cat
            currentOnFavoriteClick = onFavoriteClick
            sivCat.load(cat.url)

            val breed = cat.breeds?.firstOrNull()

            tvTitleImg.text = breed?.name ?: "Gatinho sem raça definida"
            tvSubtitleImg.text = breed?.origin ?: "Sem informações"

            if (cat.isFavorite) {
                ivFavoriteIcon.setColorFilter(
                    ContextCompat.getColor(root.context, R.color.red_300)
                )
                ivCircleShape.setColorFilter(Color.WHITE)
            } else {
                ivFavoriteIcon.clearColorFilter()
                ivCircleShape.clearColorFilter()
            }
            ivFavoriteIcon.setOnClickListener { view ->
                AnimationUtils.animatePop(view)
                onFavoriteClick(cat)
            }
            sivCat.setOnClickListener(doubleClick)
        }
    }
}
