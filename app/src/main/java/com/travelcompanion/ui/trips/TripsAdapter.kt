package com.travelcompanion.ui.trips

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.travelcompanion.R
import com.travelcompanion.databinding.ItemTripBinding
import com.travelcompanion.domain.model.Trip
import com.travelcompanion.domain.model.TripType
import com.travelcompanion.utils.DateUtils
import com.travelcompanion.utils.GenericDiffCallback
import java.util.Locale

class TripsAdapter(
    private val onTripClick: (Trip) -> Unit,
    private val onTripLongClick: (Trip) -> Unit = {}
) : ListAdapter<Trip, TripsAdapter.TripViewHolder>(
    GenericDiffCallback(
        areItemsTheSame = { old, new -> old.id == new.id },
        areContentsTheSame = { old, new -> old == new }
    )
) {

    private data class TypeStyle(
        val bgColorRes: Int,
        val accentColorRes: Int,
        val iconRes: Int,
        val labelRes: Int
    )

    private fun styleForType(type: TripType) = when (type) {
        TripType.LOCAL -> TypeStyle(R.color.stat_time_bg, R.color.trip_local, R.drawable.ic_location, R.string.trip_type_local)
        TripType.DAY_TRIP -> TypeStyle(R.color.stat_distance_bg, R.color.trip_day, R.drawable.ic_road, R.string.trip_type_day_trip)
        TripType.MULTI_DAY -> TypeStyle(R.color.stat_trips_bg, R.color.trip_multi_day, R.drawable.ic_trip, R.string.trip_type_multi_day)
        else -> TypeStyle(R.color.stat_photos_bg, R.color.trip_adventure, R.drawable.ic_map, R.string.trip_type_adventure)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val binding = ItemTripBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TripViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TripViewHolder(private val binding: ItemTripBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(trip: Trip) {
            val ctx = binding.root.context
            val style = styleForType(trip.tripType)

            binding.tvDestination.text = trip.destination
            binding.tvDates.text = DateUtils.formatDateRange(trip.startDate, trip.endDate)
            binding.tvDistance.text = String.format(Locale.getDefault(), "%.1f km", trip.totalDistance)
            binding.tvDuration.text = DateUtils.formatDuration(trip.totalDuration)
            binding.tvPhotoCount.text = trip.photoCount.toString()

            // Icona con colore corrispondente al tipo di viaggio
            binding.cardThumbnail.setCardBackgroundColor(ContextCompat.getColor(ctx, style.bgColorRes))
            Glide.with(ctx).clear(binding.ivThumbnail)
            binding.ivThumbnail.apply {
                scaleType = ImageView.ScaleType.CENTER_INSIDE
                val pad = (22 * resources.displayMetrics.density).toInt()
                setPadding(pad, pad, pad, pad)
                setImageResource(style.iconRes)
                setColorFilter(ContextCompat.getColor(ctx, style.accentColorRes), android.graphics.PorterDuff.Mode.SRC_IN)
            }

            binding.chipTripType.text = ctx.getString(style.labelRes)
            binding.chipTripType.chipBackgroundColor = ColorStateList.valueOf(
                ContextCompat.getColor(ctx, style.bgColorRes)
            )
            binding.chipTripType.setTextColor(ContextCompat.getColor(ctx, style.accentColorRes))

            binding.root.setOnClickListener { onTripClick(trip) }
            binding.root.setOnLongClickListener { onTripLongClick(trip); true }
        }
    }
}

