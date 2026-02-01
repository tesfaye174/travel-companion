package com.travelcompanion.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.travelcompanion.databinding.ItemHeroImageBinding

class HeroImageAdapter(
    private val imageResIds: List<Int>
) : RecyclerView.Adapter<HeroImageAdapter.HeroViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeroViewHolder {
        val binding = ItemHeroImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HeroViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HeroViewHolder, position: Int) {
        holder.bind(imageResIds[position])
    }

    override fun getItemCount(): Int = imageResIds.size

    class HeroViewHolder(
        private val binding: ItemHeroImageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(imageResId: Int) {
            Glide.with(binding.root.context)
                .load(imageResId)
                .centerCrop()
                .into(binding.ivHeroItem)
        }
    }
}
