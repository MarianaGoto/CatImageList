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
import com.marianagoto.catimagelist.ui.vo.CatImageVO
import com.marianagoto.catimagelist.ui.util.AnimationUtils
import com.marianagoto.catimagelist.ui.vo.CatItemVO


class HomeCatAdapter(
    private val onFavoriteClick: (CatItemVO) -> Unit
) : ListAdapter<CatItemVO, HomeCatAdapter.VH>(DIFF_CALLBACK) {
    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CatItemVO>() {
            override fun areItemsTheSame(old: CatItemVO, new: CatItemVO) = old.image.id == new.image.id

            override fun areContentsTheSame(old: CatItemVO, new: CatItemVO) = old == new
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
        private var currentCat: CatItemVO? = null
        private var currentOnFavoriteClick: ((CatItemVO) -> Unit)? = null
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
                    currentOnFavoriteClick?.invoke(cat)
                }

            }
        })

        fun bind(cat: CatItemVO, onFavoriteClick: (CatItemVO) -> Unit) = with(binding) {
            currentCat = cat
            currentOnFavoriteClick = onFavoriteClick
            sivCat.load(cat.image.url)

            val breed = cat.image.breeds.firstOrNull()

            tvTitleImg.text = breed?.name ?: "Gatinho sem raça definida"
            tvSubtitleImg.text = breed?.origin ?: "Sem informações"
            updateFavoriteUI(cat.isFavorite)

            ivFavoriteIcon.setOnClickListener { view ->
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastClickTime < 500) return@setOnClickListener
                lastClickTime = currentTime

                AnimationUtils.animatePop(view)
                onFavoriteClick(cat)
            }
            sivCat.setOnClickListener(doubleClick)
        }
        private fun updateFavoriteUI(isFavorite: Boolean) {
            if (isFavorite) {
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
