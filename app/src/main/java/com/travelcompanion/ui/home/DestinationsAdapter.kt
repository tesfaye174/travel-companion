package com.travelcompanion.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.travelcompanion.R
import com.travelcompanion.databinding.ItemDestinationBinding

class DestinationsAdapter(
    private val onDestinationClick: (Destination) -> Unit
) : ListAdapter<Destination, DestinationsAdapter.ViewHolder>(DiffCallback()) {

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
                .load(destination.imageUrl)
                .centerCrop()
                .placeholder(R.drawable.placeholder_image)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.ivDestination)
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<Destination>() {
        override fun areItemsTheSame(oldItem: Destination, newItem: Destination): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Destination, newItem: Destination): Boolean {
            return oldItem == newItem
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
        Destination(
            id = 1,
            city = "New York",
            country = "USA",
            imageUrl = "https://images.unsplash.com/photo-1485738422979-f5c462d49f74?w=800"
        ),
        Destination(
            id = 2,
            city = "Paris",
            country = "France",
            imageUrl = "https://images.unsplash.com/photo-1502602898657-3e91760cbb34?w=800"
        ),
        Destination(
            id = 3,
            city = "Torino",
            country = "Italia",
            imageUrl = "https://images.unsplash.com/photo-1568316674871-82cc9da4ab41?w=800"
        ),
        Destination(
            id = 4,
            city = "Bologna",
            country = "Italia",
            imageUrl = "https://images.unsplash.com/photo-1568973427107-9d9162f56aa6?w=800"
        ),
        Destination(
            id = 5,
            city = "Madrid",
            country = "Spain",
            imageUrl = "https://images.unsplash.com/photo-1539037116277-4db20889f2d4?w=800"
        ),
        Destination(
            id = 6,
            city = "Rome",
            country = "Italia",
            imageUrl = "https://images.unsplash.com/photo-1552832230-c0197dd311b5?w=800"
        ),
        Destination(
            id = 7,
            city = "London",
            country = "UK",
            imageUrl = "https://images.unsplash.com/photo-1513635269975-59663e0ac1ad?w=800"
        ),
        Destination(
            id = 8,
            city = "Barcelona",
            country = "Spain",
            imageUrl = "https://images.unsplash.com/photo-1583422409516-2895a77efded?w=800"
        )
    )
}
