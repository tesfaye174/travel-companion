package com.travelcompanion.ui.tracking

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.travelcompanion.databinding.ActivityTrackingJourneyBinding
import com.travelcompanion.R
import com.travelcompanion.domain.model.Note
import com.travelcompanion.domain.model.PhotoNote
import com.travelcompanion.domain.repository.ITripRepository
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.Date
import javax.inject.Inject

@AndroidEntryPoint
class TrackingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTrackingJourneyBinding

    @Inject
    lateinit var repository: ITripRepository

    private var tripId: Long = -1
    private var lastLat: Double? = null
    private var lastLon: Double? = null

    private var pendingPhotoUri: Uri? = null
    private var pendingPhotoFile: File? = null

    private val locationUpdatesReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action != TrackingService.ACTION_LOCATION_UPDATE) return
            lastLat = intent.getDoubleExtra("lat", Double.NaN).takeIf { !it.isNaN() }
            lastLon = intent.getDoubleExtra("lon", Double.NaN).takeIf { !it.isNaN() }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrackingJourneyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tripId = intent.getLongExtra(TrackingService.EXTRA_TRIP_ID, -1)

        setupToolbar()
        setupListeners()

        ensureNotificationPermissionIfNeeded()
        startTrackingIfPossible()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun setupListeners() {
        binding.fabStopTracking.setOnClickListener {
            stopTracking()
            finish()
        }

        // `fragment_tracking` is included in the activity layout; find its buttons by id
        val btnAddPhoto = findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_add_photo)
        val btnAddNote = findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_add_note)

        btnAddPhoto?.setOnClickListener {
            capturePhoto()
        }

        btnAddNote?.setOnClickListener {
            promptAddNote()
        }
    }

    private fun startTrackingIfPossible() {
        if (tripId <= 0) return
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                3001
            )
            return
        }

        val intent = Intent(this, TrackingService::class.java).apply {
            putExtra(TrackingService.EXTRA_TRIP_ID, tripId)
        }
        ContextCompat.startForegroundService(this, intent)
    }

    private fun stopTracking() {
        stopService(Intent(this, TrackingService::class.java))
    }

    private fun ensureNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 3002)
            }
        }
    }

    private fun capturePhoto() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 3003)
            return
        }
        if (tripId <= 0) return

        val file = File.createTempFile("photo_${tripId}_", ".jpg", cacheDir)
        val uri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", file)
        pendingPhotoFile = file
        pendingPhotoUri = uri

        val intent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri)
            addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        @Suppress("DEPRECATION")
        // This method is deprecated in AndroidX but retained for simplicity.
        startActivityForResult(intent, 4001)
    }

    @Deprecated("Deprecated in AndroidX; kept for simplicity")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != 4001) return
        if (resultCode != RESULT_OK) {
            pendingPhotoFile?.delete()
            pendingPhotoFile = null
            pendingPhotoUri = null
            return
        }

        val file = pendingPhotoFile ?: return
        promptAddPhotoNote(file)
    }

    private fun promptAddPhotoNote(photoFile: File) {
        val input = TextInputEditText(this).apply { hint = "Nota (opzionale)" }
        MaterialAlertDialogBuilder(this)
            .setTitle("Aggiungi Foto")
            .setView(input)
            .setPositiveButton("Salva") { _, _ ->
                val noteText = input.text?.toString().orEmpty()
                val note = PhotoNote(
                    tripId = tripId,
                    imagePath = photoFile.absolutePath,
                    note = noteText,
                    latitude = lastLat,
                    longitude = lastLon,
                    timestamp = Date()
                )
                lifecycleScope.launch(Dispatchers.IO) {
                    repository.insertPhotoNote(note)
                    val trip = repository.getTripById(tripId) ?: return@launch
                    repository.updateTrip(trip.copy(photoCount = trip.photoCount + 1))
                }
            }
            .setNegativeButton("Annulla", null)
            .show()
    }

    private fun promptAddNote() {
        if (tripId <= 0) return
        val input = TextInputEditText(this).apply { hint = "Scrivi una nota" }
        MaterialAlertDialogBuilder(this)
            .setTitle("Nuova Nota")
            .setView(input)
            .setPositiveButton("Salva") { _, _ ->
                val text = input.text?.toString()?.trim().orEmpty()
                if (text.isEmpty()) return@setPositiveButton
                val note = Note(
                    tripId = tripId,
                    content = text,
                    latitude = lastLat,
                    longitude = lastLon,
                    timestamp = Date()
                )
                lifecycleScope.launch(Dispatchers.IO) {
                    repository.insertNote(note)
                }
            }
            .setNegativeButton("Annulla", null)
            .show()
    }

    override fun onStart() {
        super.onStart()
        try {
            ContextCompat.registerReceiver(
                this,
                locationUpdatesReceiver,
                IntentFilter(TrackingService.ACTION_LOCATION_UPDATE),
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
        } catch (ex: SecurityException) {
            // On some platform versions strict export checks can throw; avoid crashing the activity.
        }
    }

    override fun onStop() {
        super.onStop()
        try {
            unregisterReceiver(locationUpdatesReceiver)
        } catch (ex: IllegalArgumentException) {
            // receiver not registered or already unregistered
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}

