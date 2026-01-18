package com.example.travelcompanion.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.travelcompanion.databinding.ItemTripBinding
import com.example.travelcompanion.domain.model.Trip
import java.text.SimpleDateFormat
import java.util.*

class PagingTripAdapter(
    private val onTripClick: (Trip) -> Unit,
    private val onTripLongClick: (Trip) -> Unit
) : PagingDataAdapter<Trip, PagingTripAdapter.TripViewHolder>(TripComparator) {

    inner class TripViewHolder(
        private val binding: ItemTripBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(trip: Trip?) {
            trip ?: return

            binding.textTripTitle.text = trip.title
            binding.textTripDates.text = formatDates(trip.startDate, trip.endDate)
            binding.textTripType.text = getTripTypeString(trip.tripType)
            binding.textTripDestination.text = trip.destination
            binding.textTripDuration.text = trip.getFormattedDuration()
            binding.textTripPhotos.text = "${trip.photoCount} foto"

            binding.root.setOnClickListener {
                onTripClick(trip)
            }

            binding.root.setOnLongClickListener {
                onTripLongClick(trip)
                true
            }
        }
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
        holder.bind(getItem(position))
    }

    object TripComparator : DiffUtil.ItemCallback<Trip>() {
        override fun areItemsTheSame(oldItem: Trip, newItem: Trip): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Trip, newItem: Trip): Boolean {
            return oldItem == newItem
        }
    }

    private fun formatDates(start: Date, end: Date): String {
        val dateFormat = SimpleDateFormat("dd MMM", Locale.ITALIAN)
        return "${dateFormat.format(start)} – ${dateFormat.format(end)} " +
                SimpleDateFormat("yyyy", Locale.ITALIAN).format(end)
    }

    private fun getTripTypeString(type: TripType): String = when(type) {
        TripType.LOCAL -> "Locale"
        TripType.DAY_TRIP -> "Giornaliero"
        TripType.MULTI_DAY -> "Multi-giorno"
    }
}