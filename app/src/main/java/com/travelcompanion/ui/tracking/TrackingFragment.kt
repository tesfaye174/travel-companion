package com.travelcompanion.ui.tracking

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.travelcompanion.R
import com.travelcompanion.databinding.FragmentTrackingBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Fragment that displays real-time GPS tracking during a trip.
 * Shows a map with the current location, route polyline, and tracking statistics.
 */
@AndroidEntryPoint
class TrackingFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentTrackingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TrackingViewModel by viewModels()

    private var googleMap: GoogleMap? = null
    private var tripId: Long = -1

    // Broadcast receiver for location updates from TrackingService
    private val locationUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action != TrackingService.ACTION_LOCATION_UPDATE) return

            val lat = intent.getDoubleExtra("lat", Double.NaN)
            val lon = intent.getDoubleExtra("lon", Double.NaN)
            val speed = intent.getFloatExtra("speed", 0f)

            if (lat.isNaN() || lon.isNaN()) return

            val location = Location("broadcast").apply {
                latitude = lat
                longitude = lon
                this.speed = speed
            }

            viewModel.onLocationUpdate(location)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrackingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tripId = arguments?.getLong(ARG_TRIP_ID, -1) ?: -1

        setupMap()
        setupListeners()
        observeViewModel()

        if (tripId > 0) {
            viewModel.startTracking(tripId)
        }
    }

    private fun setupMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_tracking) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        try {
            map.isMyLocationEnabled = true
            map.uiSettings.isMyLocationButtonEnabled = true
            map.uiSettings.isZoomControlsEnabled = true
        } catch (e: SecurityException) {
            Timber.e(e, "Location permission not granted")
        }
    }

    private fun setupListeners() {
        // Stop tracking button
        binding.btnStopTracking.setOnClickListener {
            viewModel.stopTracking()
            // Stop the foreground service
            requireContext().stopService(Intent(requireContext(), TrackingService::class.java))
            // Navigate back or finish activity
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Add photo button
        binding.btnAddPhoto.setOnClickListener {
            // Delegate to parent activity for camera handling
            (activity as? TrackingActivity)?.let {
                // The activity already handles photo capture
            }
        }

        // Add note button
        binding.btnAddNote.setOnClickListener {
            // Delegate to parent activity for note handling
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Observe elapsed time
                launch {
                    viewModel.elapsedTimeSeconds.collect { _ ->
                        binding.tvTrackingTime.text = viewModel.getFormattedElapsedTime()
                    }
                }

                // Observe distance
                launch {
                    viewModel.distanceMeters.collect { _ ->
                        binding.tvDistance.text = viewModel.getFormattedDistance()
                    }
                }

                // Observe speed
                launch {
                    viewModel.currentSpeedMps.collect { _ ->
                        binding.tvSpeed.text = viewModel.getFormattedSpeed()
                    }
                }

                // Observe photo count
                launch {
                    viewModel.photoCount.collect { count ->
                        binding.tvPhotoCount.text = count.toString()
                    }
                }

                // Observe current location for map updates
                launch {
                    viewModel.currentLocation.collect { location ->
                        location?.let { updateMapLocation(it) }
                    }
                }

                // Observe location history for polyline
                launch {
                    viewModel.locationHistory.collect { locations ->
                        drawRoutePolyline(locations)
                    }
                }
            }
        }

        // Observe current trip for destination display
        viewModel.currentTrip.observe(viewLifecycleOwner) { trip ->
            binding.tvDestination.text = trip?.destination ?: getString(R.string.tracking)
        }
    }

    private fun updateMapLocation(location: Location) {
        val latLng = LatLng(location.latitude, location.longitude)
        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
    }

    private fun drawRoutePolyline(locations: List<Location>) {
        if (locations.size < 2) return

        googleMap?.clear()

        val polylineOptions = PolylineOptions()
            .width(8f)
            .color(Color.parseColor("#4285F4")) // Google blue
            .geodesic(true)

        locations.forEach { location ->
            polylineOptions.add(LatLng(location.latitude, location.longitude))
        }

        googleMap?.addPolyline(polylineOptions)
    }

    override fun onResume() {
        super.onResume()
        registerLocationReceiver()
    }

    override fun onPause() {
        super.onPause()
        unregisterLocationReceiver()
    }

    private fun registerLocationReceiver() {
        val filter = IntentFilter(TrackingService.ACTION_LOCATION_UPDATE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireContext().registerReceiver(locationUpdateReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            @Suppress("UnspecifiedRegisterReceiverFlag")
            requireContext().registerReceiver(locationUpdateReceiver, filter)
        }
    }

    private fun unregisterLocationReceiver() {
        try {
            requireContext().unregisterReceiver(locationUpdateReceiver)
        } catch (e: Exception) {
            Timber.w(e, "Receiver not registered")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_TRIP_ID = "trip_id"

        fun newInstance(tripId: Long): TrackingFragment {
            return TrackingFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_TRIP_ID, tripId)
                }
            }
        }
    }
}
