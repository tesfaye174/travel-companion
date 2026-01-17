package com.example.travelcompanion.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.travelcompanion.TravelCompanionApplication
import com.example.travelcompanion.databinding.FragmentMapBinding
import com.example.travelcompanion.ui.trips.TripsViewModel
import com.example.travelcompanion.ui.trips.TripsViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TripsViewModel by viewModels {
        TripsViewModelFactory((requireActivity().application as TravelCompanionApplication).repository)
    }

    private var googleMap: GoogleMap? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeTrips()
    }

    private fun observeTrips() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.trips.collectLatest { trips ->
                // For each trip, show markers on the map
                // This is a simplified version - in a real app you'd show the actual routes
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Set default location (Rome)
        val rome = LatLng(41.9028, 12.4964)
        googleMap?.apply {
            addMarker(MarkerOptions().position(rome).title("Rome"))
            moveCamera(CameraUpdateFactory.newLatLngZoom(rome, 10f))
        }
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