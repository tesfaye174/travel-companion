package com.travelcompanion.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
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

    companion object {
        // Color rotation for placeholder cards — resolved once at first bind
        private val CARD_COLOR_RES = intArrayOf(
            R.color.gradient_start,
            R.color.trip_local,
            R.color.trip_day,
            R.color.trip_multi_day,
            R.color.trip_adventure,
            R.color.error,
            R.color.stat_time,
            R.color.stat_photos
        )
        private const val PLACEHOLDER_TINT = 0x33FFFFFF
    }

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

        // Cached per-ViewHolder to avoid recomputing on every bind
        private val placeholderPadding =
            (32 * binding.root.resources.displayMetrics.density).toInt()

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

            if (destination.imageUrl.isBlank()) {
                val ctx = binding.root.context
                val bgColor = ContextCompat.getColor(
                    ctx,
                    CARD_COLOR_RES[destination.id % CARD_COLOR_RES.size]
                )
                binding.ivDestination.apply {
                    setBackgroundColor(bgColor)
                    scaleType = ImageView.ScaleType.CENTER_INSIDE
                    setPadding(placeholderPadding, placeholderPadding, placeholderPadding, placeholderPadding)
                    setImageResource(R.drawable.ic_map)
                    setColorFilter(PLACEHOLDER_TINT, android.graphics.PorterDuff.Mode.SRC_ATOP)
                }
            } else {
                binding.ivDestination.apply {
                    scaleType = ImageView.ScaleType.CENTER_CROP
                    setPadding(0, 0, 0, 0)
                    clearColorFilter()
                }
                Glide.with(binding.root.context)
                    .load(destination.imageUrl)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder_image)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.ivDestination)
            }
        }
    }
}

data class Destination(
    val id: Int,
    val city: String,
    val country: String,
    val imageUrl: String
)

object SuggestedDestinations {
    val destinations = listOf(
        Destination(id = 1, city = "New York", country = "USA", imageUrl = ""),
        Destination(id = 2, city = "Paris", country = "France", imageUrl = ""),
        Destination(id = 3, city = "Torino", country = "Italia", imageUrl = ""),
        Destination(id = 4, city = "Bologna", country = "Italia", imageUrl = ""),
        Destination(id = 5, city = "Madrid", country = "Spain", imageUrl = ""),
        Destination(id = 6, city = "Rome", country = "Italia", imageUrl = ""),
        Destination(id = 7, city = "London", country = "UK", imageUrl = ""),
        Destination(id = 8, city = "Barcelona", country = "Spain", imageUrl = "")
    )
}
