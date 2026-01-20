package com.travelcompanion.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.travelcompanion.R
import com.travelcompanion.ui.trips.TripsAdapter

class MyTripsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_trips)

        // Implement RecyclerView logic
        val recyclerView = findViewById<RecyclerView>(R.id.rv_trips)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = TripsAdapter { /* click handler (open trip) */ }
        recyclerView.adapter = adapter

        // TODO: Add TabLayout logic
    }
}

