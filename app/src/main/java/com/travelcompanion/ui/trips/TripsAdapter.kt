package com.travelcompanion.ui.trips

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.travelcompanion.R
import com.travelcompanion.databinding.ItemTripBinding
import com.travelcompanion.domain.model.Trip
import com.travelcompanion.utils.DateUtils

class TripsAdapter(
    private val onTripClick: (Trip) -> Unit
) : ListAdapter<Trip, TripsAdapter.TripViewHolder>(TripDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val binding = ItemTripBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TripViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TripViewHolder(private val binding: ItemTripBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(trip: Trip) {
            binding.tvDestination.text = trip.destination
            binding.chipTripType.text = trip.tripType.name.replace("_", " ")
            binding.tvDates.text = DateUtils.formatDateRange(trip.startDate, trip.endDate)
            binding.tvDistance.text = String.format("%.1f km", trip.totalDistance)
            binding.tvDuration.text = DateUtils.formatDuration(trip.totalDuration)

            // Load thumbnail: prefer a sample resource named "colosseum" if present,
            // otherwise fall back to the placeholder. Use rounded corners.
            val radiusDp = 8
            val density = binding.ivThumbnail.resources.displayMetrics.density
            val radiusPx = (radiusDp * density).toInt()


            val imageToLoad = R.drawable.colosseum

            Glide.with(binding.root.context)
                .load(imageToLoad)
                .centerCrop()
                .transform(com.bumptech.glide.load.resource.bitmap.RoundedCorners(radiusPx))
                .into(binding.ivThumbnail)

            // Show photo count if available
            binding.tvPhotoCount.text = trip.photoCount.toString()

            binding.root.setOnClickListener { onTripClick(trip) }
        }
    }

    class TripDiffCallback : DiffUtil.ItemCallback<Trip>() {
        override fun areItemsTheSame(oldItem: Trip, newItem: Trip): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Trip, newItem: Trip): Boolean {
            return oldItem == newItem
        }
    }
}

