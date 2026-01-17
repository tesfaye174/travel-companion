package com.example.travelcompanion.ui.trips

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.travelcompanion.R
import com.example.travelcompanion.databinding.ItemTripBinding
import com.example.travelcompanion.domain.model.Trip
import com.example.travelcompanion.utils.DateUtils

class TripsAdapter(
    private val onTripClick: (Trip) -> Unit,
    private val onDeleteClick: (Trip) -> Unit
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
            binding.textTripTitle.text = trip.title
            binding.textTripDates.text = DateUtils.formatDateRange(trip.startDate, trip.endDate)
            binding.textTripDestination.text = trip.destination
            binding.textTripType.text = trip.tripType.name.replace("_", " ")

            if (trip.coverImageUrl != null) {
                Glide.with(binding.root.context)
                    .load(trip.coverImageUrl)
                    .placeholder(R.drawable.placeholder_trip)
                    .into(binding.imageTripCover)
            } else {
                binding.imageTripCover.setImageResource(R.drawable.placeholder_trip)
            }

            binding.root.setOnClickListener {
                onTripClick(trip)
            }

            binding.buttonDelete.setOnClickListener {
                onDeleteClick(trip)
            }
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