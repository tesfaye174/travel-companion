package com.travelcompanion.ui.tips

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.travelcompanion.databinding.ItemTipBinding
import com.travelcompanion.utils.GenericDiffCallback

class TipsAdapter(
    private val onTipClick: (TravelTip) -> Unit
) : ListAdapter<TravelTip, TipsAdapter.ViewHolder>(
    GenericDiffCallback(
        areItemsTheSame = { old, new -> old.id == new.id },
        areContentsTheSame = { old, new -> old == new }
    )
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTipBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemTipBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onTipClick(getItem(position))
                }
            }
        }

        fun bind(tip: TravelTip) {
            binding.tvTipTitle.text = tip.title
            binding.tvTipDescription.text = tip.description
            binding.ivTipIcon.setImageResource(tip.iconRes)

            // Set category chip
            binding.chipCategory.text = when (tip.category) {
                TipCategory.PACKING -> binding.root.context.getString(com.travelcompanion.R.string.tip_category_packing)
                TipCategory.SAFETY -> binding.root.context.getString(com.travelcompanion.R.string.tip_category_safety)
                TipCategory.BUDGET -> binding.root.context.getString(com.travelcompanion.R.string.tip_category_budget)
                TipCategory.CULTURE -> binding.root.context.getString(com.travelcompanion.R.string.tip_category_culture)
                TipCategory.TRANSPORT -> binding.root.context.getString(com.travelcompanion.R.string.tip_category_transport)
            }
        }
    }
}
