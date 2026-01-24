package com.travelcompanion.ui.tripdetails

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.snackbar.Snackbar
import com.travelcompanion.R
import com.travelcompanion.databinding.FragmentTripDetailsBinding
import com.travelcompanion.domain.model.Trip
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class TripDetailsFragment : Fragment() {

    private var _binding: FragmentTripDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var photoAdapter: PhotoAdapter
    private lateinit var noteAdapter: NoteAdapter

    private val viewModel: TripDetailsViewModel by viewModels()
    private var googleMap: GoogleMap? = null
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private val dateTimeFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())

    private var currentPhotoUri: Uri? = null
    private var currentPhotoPath: String? = null
    private var currentTrip: Trip? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            launchCamera()
        } else {
            Toast.makeText(requireContext(), R.string.camera_permission_required, Toast.LENGTH_SHORT).show()
        }
    }

    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            currentPhotoPath?.let { path ->
                savePhoto(path)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTripDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        setupToolbar()
        setupRecyclerViews()
        setupMap()
        setupClickListeners()

        val tripId = arguments?.getLong("tripId", -1L) ?: -1L
        if (tripId <= 0L) {
            binding.tvDestination.text = "Invalid trip"
            binding.chipTripType.text = ""
            binding.tvDates.text = ""
            binding.tvDistance.text = ""
            binding.tvDuration.text = ""
            Snackbar.make(binding.root, "Missing tripId - open a trip from the list", Snackbar.LENGTH_LONG).show()
            return
        }
        viewModel.setTripId(tripId)
        observeViewModel()
    }

    private fun setupClickListeners() {
        // FAB Edit Trip
        binding.fabEdit.setOnClickListener {
            currentTrip?.let { trip ->
                EditTripDialogFragment.newInstance(trip) { updatedTrip ->
                    viewModel.updateTrip(updatedTrip)
                    Snackbar.make(binding.root, R.string.settings_saved, Snackbar.LENGTH_SHORT).show()
                }.show(childFragmentManager, "EditTripDialog")
            }
        }

        // Add Note button
        binding.btnAddNote.setOnClickListener {
            AddNoteDialogFragment.newInstance { content ->
                viewModel.addNote(content)
                Snackbar.make(binding.root, R.string.settings_saved, Snackbar.LENGTH_SHORT).show()
            }.show(childFragmentManager, "AddNoteDialog")
        }

        // Add Photo button
        binding.btnAddPhoto.setOnClickListener {
            checkCameraPermissionAndLaunch()
        }
    }

    private fun checkCameraPermissionAndLaunch() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                launchCamera()
            }
            else -> {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun launchCamera() {
        try {
            val photoFile = createImageFile()
            currentPhotoPath = photoFile.absolutePath
            currentPhotoUri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                photoFile
            )
            takePictureLauncher.launch(currentPhotoUri)
        } catch (e: Exception) {
            Timber.e(e, "Error launching camera")
            Toast.makeText(requireContext(), R.string.error_saving_trip, Toast.LENGTH_SHORT).show()
        }
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = requireContext().getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }

    private fun savePhoto(imagePath: String) {
        // Get current location if available
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                viewModel.addPhotoNote(
                    imagePath = imagePath,
                    note = "",
                    latitude = location?.latitude,
                    longitude = location?.longitude
                )
                Snackbar.make(binding.root, R.string.add_photo_title, Snackbar.LENGTH_SHORT).show()
            }.addOnFailureListener {
                viewModel.addPhotoNote(imagePath, "", null, null)
                Snackbar.make(binding.root, R.string.add_photo_title, Snackbar.LENGTH_SHORT).show()
            }
        } else {
            viewModel.addPhotoNote(imagePath, "", null, null)
            Snackbar.make(binding.root, R.string.add_photo_title, Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_details) as? SupportMapFragment
        mapFragment?.getMapAsync(object : OnMapReadyCallback {
            override fun onMapReady(map: GoogleMap) {
                googleMap = map
                map.uiSettings.isZoomControlsEnabled = true
                renderRoute(viewModel.journeys.value.orEmpty())
            }
        })
    }

    private fun setupRecyclerViews() {
        photoAdapter = PhotoAdapter()
        binding.rvPhotos.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = photoAdapter
        }

        noteAdapter = NoteAdapter()
        binding.rvNotes.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = noteAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.trip.observe(viewLifecycleOwner) { trip ->
            if (trip == null) return@observe

            currentTrip = trip
            binding.tvDestination.text = trip.destination
            binding.chipTripType.text = trip.tripType.name.replace('_', ' ').lowercase().replaceFirstChar { it.titlecase() }

            val end = trip.endDate
            binding.tvDates.text = if (end != null) {
                "${dateFormat.format(trip.startDate)} - ${dateFormat.format(end)}"
            } else {
                dateFormat.format(trip.startDate)
            }
        }

        viewModel.totalDistanceKm.observe(viewLifecycleOwner) { km ->
            binding.tvDistance.text = "Distanza: %.2f km".format(km ?: 0.0)
        }

        viewModel.totalDurationMs.observe(viewLifecycleOwner) { ms ->
            val durationText = DateUtils.formatElapsedTime(((ms ?: 0L) / 1000L))
            binding.tvDuration.text = "Durata: $durationText"
        }

        viewModel.photoNotes.observe(viewLifecycleOwner) { photos ->
            val photoList = photos.orEmpty()
            val items = photoList.map {
                PhotoAdapter.PhotoItem(
                    imageUrl = it.imagePath,
                    caption = it.note.ifBlank { "Foto" }
                )
            }
            photoAdapter.submitList(items)
            
            // Update photo count
            binding.tvPhotoCount.text = photoList.size.toString()
        }

        viewModel.notes.observe(viewLifecycleOwner) { notes ->
            val items = notes.orEmpty().map {
                NoteAdapter.NoteItem(
                    content = it.content,
                    date = dateTimeFormat.format(it.timestamp)
                )
            }
            noteAdapter.submitList(items)
        }

        viewModel.journeys.observe(viewLifecycleOwner) { journeys ->
            renderRoute(journeys.orEmpty())
        }
    }

    private fun renderRoute(journeys: List<com.travelcompanion.domain.model.Journey>) {
        val map = googleMap ?: return
        map.clear()

        val allPoints = journeys.flatMap { j -> j.coordinates.map { LatLng(it.latitude, it.longitude) } }
        if (allPoints.size >= 2) {
            map.addPolyline(
                PolylineOptions()
                    .addAll(allPoints)
                    .width(8f)
            )
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(allPoints.first(), 12f))
        } else if (allPoints.size == 1) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(allPoints.first(), 14f))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

