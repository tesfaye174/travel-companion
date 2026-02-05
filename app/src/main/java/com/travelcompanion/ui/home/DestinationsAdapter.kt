package com.travelcompanion.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.travelcompanion.R
import com.travelcompanion.databinding.ItemDestinationBinding
import com.travelcompanion.utils.GenericDiffCallback

class DestinationsAdapter(
    private val onDestinationClick: (Destination) -> Unit
) : ListAdapter<Destination, DestinationsAdapter.ViewHolder>(
    GenericDiffCallback(
        areItemsTheSame = { old, new -> old.id == new.id },
        areContentsTheSame = { old, new -> old == new }
    )
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDestinationBinding.inflate(
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
        private val binding: ItemDestinationBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onDestinationClick(getItem(position))
                }
            }
        }

        fun bind(destination: Destination) {
            binding.tvDestinationName.text = destination.city
            binding.tvDestinationCountry.text = destination.country

            // Accessibility: set content description on the image
            binding.ivDestination.contentDescription = "${destination.city}, ${destination.country}"
            // Transition name so shared element transitions can use it if needed
            binding.ivDestination.transitionName = "destination_image_${destination.id}"

            val ctx = binding.root.context

            // Priorità: imageUrl (remoto) -> drawable locale (imageResName) -> placeholder
            val imageUrl = destination.imageUrl
            if (!imageUrl.isNullOrBlank()) {
                Glide.with(ctx)
                    .load(imageUrl)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.placeholder_image)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.ivDestination)
                return
            }

            val resId = destination.imageResName?.let { name ->
                ctx.resources.getIdentifier(name, "drawable", ctx.packageName)
            } ?: 0

            if (resId != 0) {
                Glide.with(ctx)
                    .load(resId)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder_image)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.ivDestination)
            } else {
                Glide.with(ctx)
                    .load(R.drawable.placeholder_image)
                    .centerCrop()
                    .into(binding.ivDestination)
            }
        }
    }
}
