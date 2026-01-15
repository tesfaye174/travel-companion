package com.example.travelcompanion.ui.journey

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.travelcompanion.databinding.FragmentJourneyBinding

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.travelcompanion.TravelCompanionApplication
import com.example.travelcompanion.utils.TrackingService
import com.example.travelcompanion.utils.CameraHelper
import com.example.travelcompanion.domain.model.Photo
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class JourneyFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentJourneyBinding? = null
    private val binding get() = _binding!!

    private var googleMap: GoogleMap? = null
    private var cameraHelper: CameraHelper? = null
    
    private val viewModel: JourneyViewModel by viewModels {
        JourneyViewModelFactory((requireActivity().application as TravelCompanionApplication).repository)
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
            permissions[Manifest.permission.CAMERA] == true) {
            setupTracking()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentJourneyBinding.inflate(inflater, container, false)
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)
        cameraHelper = CameraHelper(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.buttonToggleTracking.setOnClickListener {
            checkPermissionsAndToggle()
        }

        binding.buttonTakePhoto.setOnClickListener {
            cameraHelper?.takePhoto { uri ->
                val journey = viewModel.currentJourney.value ?: return@takePhoto
                val photo = Photo(
                    tripId = journey.tripId,
                    journeyId = journey.id,
                    pointId = null, // Could link to latest point
                    filePath = uri.toString(),
                    timestamp = System.currentTimeMillis()
                )
                viewModel.insertPhoto(photo)
            }
        }
    }

    private fun checkPermissionsAndToggle() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA
        )
        if (permissions.all { ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED }) {
            setupTracking()
        } else {
            requestPermissionLauncher.launch(permissions)
        }
    }

    private fun setupTracking() {
        val activeTripId = 1L // For demo/initial test
        lifecycleScope.launch {
            viewModel.currentJourney.collectLatest { journey ->
                if (journey == null) {
                    binding.buttonToggleTracking.text = "Start Journey"
                    binding.textStatus.text = "Ready"
                    binding.cameraLayout.visibility = View.GONE
                    binding.buttonTakePhoto.visibility = View.GONE
                    stopTrackingService()
                } else {
                    binding.buttonToggleTracking.text = "Stop Journey"
                    binding.textStatus.text = "Tracking..."
                    binding.cameraLayout.visibility = View.VISIBLE
                    binding.buttonTakePhoto.visibility = View.VISIBLE
                    cameraHelper?.startCamera(viewLifecycleOwner, binding.previewView)
                    startTrackingService(journey.id)
                    observePoints(journey.id)
                }
            }
        }
        
        if (viewModel.currentJourney.value == null) {
            viewModel.startJourney(activeTripId)
        } else {
            viewModel.stopJourney()
        }
    }

    private fun startTrackingService(journeyId: Long) {
        val intent = Intent(requireContext(), TrackingService::class.java).apply {
            putExtra(TrackingService.EXTRA_JOURNEY_ID, journeyId)
        }
        ContextCompat.startForegroundService(requireContext(), intent)
    }

    private fun stopTrackingService() {
        val intent = Intent(requireContext(), TrackingService::class.java)
        requireContext().stopService(intent)
    }

    private fun observePoints(journeyId: Long) {
        viewModel.getPointsForJourney(journeyId).observe(viewLifecycleOwner) { points ->
            val path = points.map { LatLng(it.latitude, it.longitude) }
            googleMap?.addPolyline(PolylineOptions().addAll(path))
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.mapView.onDestroy()
        _binding = null
    }
}
