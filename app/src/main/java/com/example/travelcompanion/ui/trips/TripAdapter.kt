package com.travelcompanion.ui.trips

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.travelcompanion.R
import com.travelcompanion.databinding.ItemTripBinding

class TripAdapter : RecyclerView.Adapter<TripAdapter.TripViewHolder>() {

    private var trips = listOf<TripItem>()

    fun submitList(trips: List<TripItem>) {
        this.trips = trips
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val binding = ItemTripBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TripViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        holder.bind(trips[position])
    }

    override fun getItemCount() = trips.size

    inner class TripViewHolder(private val binding: ItemTripBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(trip: TripItem) {
            binding.tvDestination.text = trip.destination
            binding.tvTripType.text = trip.type
            binding.tvDates.text = trip.dates
            binding.tvDistance.text = trip.distance
            binding.tvDuration.text = trip.duration
        }
    }

    data class TripItem(
        val destination: String,
        val type: String,
        val dates: String,
        val distance: String,
        val duration: String
    )
}