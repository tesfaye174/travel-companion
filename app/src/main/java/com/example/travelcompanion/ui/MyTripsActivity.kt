package com.example.travelcompanion.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.travelcompanion.R
import com.example.travelcompanion.data.TripsAdapter

class MyTripsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_trips)

        // Implement RecyclerView logic
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view_trips)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = TripsAdapter(TripRepository.getTrips())

        // TODO: Add TabLayout logic
    }
}