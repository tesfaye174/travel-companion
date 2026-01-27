package com.travelcompanion.ui.tracking

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.provider.Settings
import android.net.Uri
import android.os.Bundle
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.activity.result.contract.ActivityResultContracts
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

    companion object {
        private const val REQ_FINE_LOCATION = 3001
        private const val REQ_NOTIFICATION = 3002
        private const val REQ_CAMERA = 3003
        private const val REQ_BACKGROUND_LOCATION = 3004
    }

    private lateinit var binding: ActivityTrackingJourneyBinding

    @Inject
    lateinit var repository: ITripRepository

    private var tripId: Long = -1
    private var lastLat: Double? = null
    private var lastLon: Double? = null

    private var pendingPhotoUri: Uri? = null
    private var pendingPhotoFile: File? = null

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (!success) {
            pendingPhotoFile?.delete()
            pendingPhotoFile = null
            pendingPhotoUri = null
            return@registerForActivityResult
        }

        val file = pendingPhotoFile ?: return@registerForActivityResult
        promptAddPhotoNote(file)
    }

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
                REQ_FINE_LOCATION
            )
            return
        }

        // If background location is required on this platform, ensure it's granted.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION), REQ_BACKGROUND_LOCATION)
            return
        }

        startTrackingService()
    }

    private fun startTrackingService() {
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
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQ_NOTIFICATION)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQ_FINE_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Fine/coarse granted — check background on newer platforms
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION), REQ_BACKGROUND_LOCATION)
                    } else {
                        startTrackingService()
                    }
                }
            }
            REQ_BACKGROUND_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startTrackingService()
                } else {
                    MaterialAlertDialogBuilder(this)
                        .setTitle(R.string.permission_needed)
                        .setMessage(R.string.background_location_explanation)
                        .setPositiveButton(R.string.open_settings) { _, _ ->
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName"))
                            startActivity(intent)
                        }
                        .setNegativeButton(R.string.cancel, null)
                        .show()
                }
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

        // Use Activity Result API to capture a photo into the provided URI.
        takePictureLauncher.launch(uri)
    }
    

    private fun promptAddPhotoNote(photoFile: File) {
        val input = TextInputEditText(this).apply { hint = getString(R.string.note_optional_hint) }
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.add_photo_title)
            .setView(input)
            .setPositiveButton(R.string.save) { _, _ ->
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
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun promptAddNote() {
        if (tripId <= 0) return
        val input = TextInputEditText(this).apply { hint = getString(R.string.write_note_hint) }
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.new_note_title)
            .setView(input)
            .setPositiveButton(R.string.save) { _, _ ->
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
            .setNegativeButton(R.string.cancel, null)
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

