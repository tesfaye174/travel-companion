package com.travelcompanion.ui.tracking

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import com.travelcompanion.R
import com.travelcompanion.databinding.FragmentTrackingBinding
import com.travelcompanion.ui.map.MapManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Fragment that displays real-time GPS tracking during a trip.
 * Shows a map with the current location, route polyline, and tracking statistics.
 */
@AndroidEntryPoint
class TrackingFragment : Fragment() {

    private var _binding: FragmentTrackingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TrackingViewModel by viewModels()

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
        // Configure osmdroid
        org.osmdroid.config.Configuration.getInstance().load(requireContext(), androidx.preference.PreferenceManager.getDefaultSharedPreferences(requireContext()))
        org.osmdroid.config.Configuration.getInstance().userAgentValue = requireContext().packageName
        val mapView = binding.mapTracking
        mapView.setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        // Enable user location overlay
        val userLocationOverlay = org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay(org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider(requireContext()), mapView)
        userLocationOverlay.enableMyLocation()
        userLocationOverlay.enableFollowLocation()
        mapView.overlays.add(userLocationOverlay)
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
        val mapView = binding.mapTracking
        val geoPoint = org.osmdroid.util.GeoPoint(location.latitude, location.longitude)
        MapManager.centerMap(mapView, geoPoint, 16.0)
    }

    private fun drawRoutePolyline(locations: List<Location>) {
        val mapView = binding.mapTracking
        MapManager.clearPolylines(mapView)
        if (locations.size < 2) return
        val geoPoints = locations.map { org.osmdroid.util.GeoPoint(it.latitude, it.longitude) }
        MapManager.drawPolyline(mapView, geoPoints, android.graphics.Color.parseColor("#4285F4"), 8f)
        mapView.invalidate()
    }

    override fun onResume() {
        super.onResume()
        binding.mapTracking.onResume()
        registerLocationReceiver()
    }

    override fun onPause() {
        super.onPause()
        binding.mapTracking.onPause()
        unregisterLocationReceiver()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.mapTracking.onDetach()
        _binding = null
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
        } catch (e: IllegalArgumentException) {
            Timber.w("Receiver non registrato, nessuna azione necessaria")
        } catch (e: Exception) {
            Timber.w(e, "Errore sconosciuto durante l'unregister del receiver")
        }
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
