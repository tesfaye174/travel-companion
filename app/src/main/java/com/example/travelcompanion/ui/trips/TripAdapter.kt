package com.example.travelcompanion.ui.trips

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.travelcompanion.databinding.ItemTripBinding
import com.example.travelcompanion.domain.model.Trip
import java.text.SimpleDateFormat
import java.util.*

class TripAdapter(private val onItemClick: (Trip) -> Unit) :
    ListAdapter<Trip, TripAdapter.TripViewHolder>(TripDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val binding = ItemTripBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TripViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TripViewHolder(private val binding: ItemTripBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

        fun bind(trip: Trip) {
            binding.textDestination.text = trip.destination
            binding.textDate.text = dateFormat.format(Date(trip.startDate))
            binding.textType.text = trip.type.name.replace("_", "-")
            binding.textDistance.text = String.format(Locale.getDefault(), "%.1f km", trip.totalDistance)
            binding.root.setOnClickListener { onItemClick(trip) }
        }
    }

    class TripDiffCallback : DiffUtil.ItemCallback<Trip>() {
        override fun areItemsTheSame(oldItem: Trip, newItem: Trip): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Trip, newItem: Trip): Boolean = oldItem == newItem
    }
}
