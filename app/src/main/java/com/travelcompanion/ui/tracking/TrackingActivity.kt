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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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
import org.osmdroid.config.Configuration

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

    // Launcher per catturare foto (inizializzato in onCreate)
    private lateinit var capturePhotoLauncher: ActivityResultLauncher<Uri>

    private var trackingStartTime: Long = 0L
    private var timerJob: kotlinx.coroutines.Job? = null

    private val locationUpdatesReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action != TrackingService.ACTION_LOCATION_UPDATE) return
            lastLat = intent.getDoubleExtra("lat", Double.NaN).takeIf { !it.isNaN() }
            lastLon = intent.getDoubleExtra("lon", Double.NaN).takeIf { !it.isNaN() }

            // Update distance
            val distance = intent.getFloatExtra("dist", 0f)
            binding.tvDistance.text = String.format(java.util.Locale.getDefault(), "%.1f", distance)

            // Update map position
            lastLat?.let { lat ->
                lastLon?.let { lon ->
                    val geoPoint = org.osmdroid.util.GeoPoint(lat, lon)
                    binding.mapTracking.controller.animateTo(geoPoint)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrackingJourneyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inizializzo il launcher per la fotocamera
        capturePhotoLauncher = registerForActivityResult<Uri, Boolean>(
            ActivityResultContracts.TakePicture()
        ) { success: Boolean ->
            if (success) {
                pendingPhotoFile?.let { promptAddPhotoNote(it) }
            } else {
                pendingPhotoFile?.delete()
                pendingPhotoFile = null
                pendingPhotoUri = null
            }
        }

        tripId = intent.getLongExtra(TrackingService.EXTRA_TRIP_ID, -1)

        setupToolbar()
        setupMap()
        setupListeners()
        loadTripDetails()

        ensureNotificationPermissionIfNeeded()
        startTrackingIfPossible()
    }

    private fun setupMap() {
        // Configure osmdroid
        // Usa applicationContext per evitare warning di qualificatore ridondante
        Configuration.getInstance().load(applicationContext, applicationContext.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
        Configuration.getInstance().userAgentValue = applicationContext.packageName

        binding.mapTracking.setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK)
        binding.mapTracking.setMultiTouchControls(true)
        binding.mapTracking.controller.setZoom(15.0)
    }

    private fun loadTripDetails() {
        if (tripId <= 0) return
        lifecycleScope.launch(Dispatchers.IO) {
            val trip = repository.getTripById(tripId)
            launch(Dispatchers.Main) {
                trip?.let {
                    binding.tvDestination.text = it.destination.ifBlank { it.title }
                    binding.tvPhotoCount.text = it.photoCount.toString()
                }
            }
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun setupListeners() {
        // Stop tracking button
        binding.fabStopTracking.setOnClickListener {
            stopTracking()
            finish()
        }

        // Add photo button
        binding.btnAddPhoto.setOnClickListener {
            capturePhoto()
        }

        // Add note button
        binding.btnAddNote.setOnClickListener {
            promptAddNote()
        }

        // Plus / Minus FABs: zoom in / zoom out the map (clamped)
        binding.fabPlus.setOnClickListener {
            val controller = binding.mapTracking.controller
            val currentZoom = binding.mapTracking.zoomLevelDouble
            val newZoom = (currentZoom + 1.0).coerceIn(1.0, 20.0)
            controller.setZoom(newZoom)
        }

        binding.fabMinus.setOnClickListener {
            val controller = binding.mapTracking.controller
            val currentZoom = binding.mapTracking.zoomLevelDouble
            val newZoom = (currentZoom - 1.0).coerceIn(1.0, 20.0)
            controller.setZoom(newZoom)
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

        // Start the timer
        startTimer()
    }

    private fun startTimer() {
        trackingStartTime = System.currentTimeMillis()
        timerJob = lifecycleScope.launch {
            while (true) {
                val elapsed = System.currentTimeMillis() - trackingStartTime
                val hours = (elapsed / 3600000).toInt()
                val minutes = ((elapsed % 3600000) / 60000).toInt()
                val seconds = ((elapsed % 60000) / 1000).toInt()
                binding.tvTrackingTime.text = String.format(java.util.Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
                kotlinx.coroutines.delay(1000)
            }
        }
    }

    private fun stopTracking() {
        timerJob?.cancel()
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

        // Uso ActivityResultContracts invece di startActivityForResult (moderno)
        capturePhotoLauncher.launch(uri)
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
        } catch (se: SecurityException) {
            // On some platform versions strict export checks can throw; avoid crashing the activity.
            Timber.w(se, "ContextCompat.registerReceiver security exception")
        }
    }

    override fun onStop() {
        super.onStop()
        try {
            unregisterReceiver(locationUpdatesReceiver)
        } catch (iae: IllegalArgumentException) {
            // receiver not registered or already unregistered
            Timber.w(iae, "unregisterReceiver called but receiver not registered")
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
