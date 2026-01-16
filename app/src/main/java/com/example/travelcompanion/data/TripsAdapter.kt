package com.example.travelcompanion.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.travelcompanion.R

class TripsAdapter(private val trips: List<String>) : RecyclerView.Adapter<TripsAdapter.TripViewHolder>() {

    class TripViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_trip, parent, false)
        return TripViewHolder(view)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        // TODO: Bind trip data to the view
    }

    override fun getItemCount(): Int = trips.size
}