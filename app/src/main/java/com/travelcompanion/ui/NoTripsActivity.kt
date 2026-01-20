package com.travelcompanion.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.travelcompanion.R
import android.widget.TextView

class NoTripsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_trips)

        // Add logic for empty state
        val message = findViewById<TextView>(R.id.message_no_trips)
        message.text = "No trips yet. Start planning your journey!"
    }
}

