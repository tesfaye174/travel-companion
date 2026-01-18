package com.example.travelcompanion.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.travelcompanion.R
import com.example.travelcompanion.domain.model.Trip
import com.example.travelcompanion.domain.model.TripType
import java.text.SimpleDateFormat
import java.util.Locale

class TripsAdapter(
    private val onTripClick: (Trip) -> Unit
) : RecyclerView.Adapter<TripsAdapter.TripViewHolder>() {

    private var trips = emptyList<Trip>()

    inner class TripViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.textTripTitle)
        val dates: TextView = itemView.findViewById(R.id.textTripDates)
        val type: TextView = itemView.findViewById(R.id.textTripType)
        val destination: TextView = itemView.findViewById(R.id.textTripDestination)
        val duration: TextView = itemView.findViewById(R.id.textTripDuration)
        val photos: TextView = itemView.findViewById(R.id.textTripPhotos)
        val trackingIndicator: ImageView? = itemView.findViewById(R.id.imageTrackingIndicator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_trip, parent, false)
        return TripViewHolder(view)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        val trip = trips[position]

        holder.title.text = trip.title
        holder.dates.text = formatDates(trip.startDate, trip.endDate)
        holder.type.text = getTripTypeString(trip.tripType)
        holder.destination.text = trip.destination
        holder.duration.text = trip.getFormattedDuration()
        holder.photos.text = "${trip.photoCount} foto"

        // Show tracking indicator if trip is being tracked
        holder.trackingIndicator?.visibility = if (trip.isTracking) View.VISIBLE else View.GONE

        holder.itemView.setOnClickListener {
            onTripClick(trip)
        }
    }

    override fun getItemCount(): Int = trips.size

    fun setTrips(trips: List<Trip>) {
        this.trips = trips
        notifyDataSetChanged()
    }

    private fun formatDates(start: java.util.Date, end: java.util.Date): String {
        val dateFormat = SimpleDateFormat("dd MMM", Locale.ITALIAN)
        return "${dateFormat.format(start)} – ${dateFormat.format(end)} ${SimpleDateFormat("yyyy", Locale.ITALIAN).format(end)}"
    }

    private fun getTripTypeString(type: TripType): String = when(type) {
        TripType.LOCAL -> "Locale"
        TripType.DAY_TRIP -> "Giornaliero"
        TripType.MULTI_DAY -> "Multi-giorno"
    }
}