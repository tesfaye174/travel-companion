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

            Glide.with(binding.root.context)
                .load(destination.imageResId)
                .centerCrop()
                .placeholder(R.drawable.placeholder_image)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.ivDestination)
        }
    }
}

data class Destination(
    val id: Int,
    val city: String,
    val country: String,
    val imageResId: Int
)

object SuggestedDestinations {
    val destinations = listOf(
        Destination(
            id = 1,
            city = "New York",
            country = "USA",
            imageResId = R.drawable.destination_new_york
        ),
        Destination(
            id = 2,
            city = "Paris",
            country = "France",
            imageResId = R.drawable.destination_paris
        ),
        Destination(
            id = 3,
            city = "Torino",
            country = "Italia",
            imageResId = R.drawable.destination_turin
        ),
        Destination(
            id = 4,
            city = "Bologna",
            country = "Italia",
            imageResId = R.drawable.destination_emilia_romagna
        ),
        Destination(
            id = 5,
            city = "Madrid",
            country = "Spain",
            imageResId = R.drawable.destination_madrid
        ),
        Destination(
            id = 6,
            city = "Rome",
            country = "Italia",
            imageResId = R.drawable.destination_rome_colosseum
        ),
        Destination(
            id = 7,
            city = "London",
            country = "UK",
            imageResId = R.drawable.destination_dream_city
        ),
        Destination(
            id = 8,
            city = "Barcelona",
            country = "Spain",
            imageResId = R.drawable.destination_paris_arc
        )
    )
}
