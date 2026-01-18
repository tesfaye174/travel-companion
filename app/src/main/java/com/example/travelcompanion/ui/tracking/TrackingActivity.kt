package com.travelcompanion.ui.tracking

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.travelcompanion.databinding.ActivityTrackingJourneyBinding

class TrackingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTrackingJourneyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrackingJourneyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupListeners()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun setupListeners() {
        binding.fabStopTracking.setOnClickListener {
            // Stop tracking
            finish()
        }

        binding.btnAddPhoto.setOnClickListener {
            // Add photo
        }

        binding.btnAddNote.setOnClickListener {
            // Add note
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}