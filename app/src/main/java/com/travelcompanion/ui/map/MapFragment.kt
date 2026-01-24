package com.travelcompanion.ui.map

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import timber.log.Timber
import dagger.hilt.android.AndroidEntryPoint
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.travelcompanion.databinding.FragmentMapBinding
import com.travelcompanion.utils.GeofenceHelper
import com.travelcompanion.utils.PermissionUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import android.widget.LinearLayout
import android.content.pm.PackageManager
import com.google.android.material.snackbar.Snackbar
import java.text.DateFormat
import java.util.Date
import java.util.UUID

@AndroidEntryPoint
class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    // Use fragment-scoped ViewModel so Hilt's fragment factory provides the correct ViewModelProvider
    private val viewModel: MapViewModel by viewModels()
    private var googleMap: GoogleMap? = null

    private val polylines = mutableListOf<Polyline>()
    private var showRoutePoints = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Diagnostic logging: which fragment and which ViewModel provider factories are present
        try {
            Timber.d("this=${this::class.java.name} defaultFactory=${defaultViewModelProviderFactory::class.java.name} activityFactory=${requireActivity().defaultViewModelProviderFactory::class.java.name}")
        } catch (t: Throwable) {
            Timber.d(t, "failed to read factories")
        }

        setupListeners()

        val mapFragment = childFragmentManager.findFragmentById(com.travelcompanion.R.id.map_container) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        viewModel.loadJourneysForMap()
        viewModel.loadGeofenceAreas()
        viewModel.loadGeofenceEvents()
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isMyLocationButtonEnabled = false

        // Long-press to define a geofence area (meets geofencing requirement)
        map.setOnMapLongClickListener { latLng ->
            promptAddGeofence(latLng)
        }

        observeAndRender()
    }

    private fun observeAndRender() {
        viewModel.journeys.observe(viewLifecycleOwner) { journeys ->
            renderJourneys(journeys)
        }
        viewModel.geofenceAreas.observe(viewLifecycleOwner) { areas ->
            renderGeofences(areas)
        }
    }

    private fun renderJourneys(journeys: List<com.travelcompanion.domain.model.Journey>) {
        val map = googleMap ?: return
        polylines.forEach { it.remove() }
        polylines.clear()
        map.clear()

        // map.clear() also wipes geofence circles; restore them immediately.
        renderGeofences(viewModel.geofenceAreas.value.orEmpty())

        // Draw routes
        journeys.forEach { journey ->
            val points = journey.coordinates.map { LatLng(it.latitude, it.longitude) }
            if (points.size >= 2) {
                val polyline = map.addPolyline(
                    PolylineOptions()
                        .addAll(points)
                        .width(8f)
                )
                polylines.add(polyline)

                if (showRoutePoints) {
                    points.forEach { p ->
                        map.addMarker(MarkerOptions().position(p).title("Trip ${journey.tripId}"))
                    }
                }
            }
        }

        // Default camera: first available coordinate
        val first = journeys.firstOrNull()?.coordinates?.firstOrNull()
        if (first != null) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(first.latitude, first.longitude), 10f))
        }
    }

    private fun renderGeofences(areas: List<com.travelcompanion.domain.model.GeofenceArea>) {
        val map = googleMap ?: return
        areas.forEach { area ->
            val center = LatLng(area.latitude, area.longitude)
            // The radius is calculated in meters and converted to double for precision.
            map.addCircle(
                CircleOptions()
                    .center(center)
                    .radius(area.radiusMeters.toDouble())
                    .strokeWidth(3f)
            )
        }
    }

    private fun setupListeners() {
        binding.fabMyLocation.setOnClickListener {
            centerOnMyLocation()
        }

        binding.fabHeatmap.setOnClickListener {
            showRoutePoints = !showRoutePoints
            viewModel.journeys.value?.let { renderJourneys(it) }
        }

        binding.fabGeofenceEvents.setOnClickListener {
            showGeofenceEventsDialog()
        }
    }

    private fun showGeofenceEventsDialog() {
        val context = context ?: return
        val events = viewModel.geofenceEvents.value.orEmpty()
        if (events.isEmpty()) {
            Snackbar.make(binding.root, "No geofence events recorded", Snackbar.LENGTH_SHORT).show()
            return
        }

        val areaNameById = viewModel.geofenceAreas.value.orEmpty().associateBy({ it.id }, { it.name })
        val df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)

        val items = events.take(50).map { e ->
            val name = areaNameById[e.geofenceId] ?: e.geofenceId
            val whenText = df.format(Date(e.timestamp))
            "${e.transition} • $name • $whenText"
        }.toTypedArray()

        MaterialAlertDialogBuilder(context)
            .setTitle("Geofence Events")
            .setItems(items, null)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun centerOnMyLocation() {
        val map = googleMap ?: return
        val context = context ?: return

        if (!PermissionUtils.hasLocationPermissions(context)) {
            Snackbar.make(binding.root, "Grant location permissions to center the map", Snackbar.LENGTH_LONG).show()
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                2001
            )
            return
        }

        val fused = LocationServices.getFusedLocationProviderClient(requireActivity())
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fused.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                map.isMyLocationEnabled = true
                val latLng = LatLng(location.latitude, location.longitude)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14f))
            }
        }
    }

    private fun promptAddGeofence(latLng: LatLng) {
        val context = context ?: return
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(binding.root, "Fine location permission required to create geofence", Snackbar.LENGTH_LONG).show()
            return
        }

        val nameInput = TextInputEditText(context).apply { hint = "Area name" }
        val radiusInput = TextInputEditText(context).apply {
            hint = "Radius (meters)"
            setText("150")
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
        }
        val container = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 16, 48, 0)
            addView(nameInput)
            addView(radiusInput)
        }

        MaterialAlertDialogBuilder(context)
            .setTitle("Create Geofence")
            .setView(container)
            .setPositiveButton("Create") { _, _ ->
                val name = nameInput.text?.toString()?.takeIf { it.isNotBlank() } ?: "Area"
                val radius = radiusInput.text?.toString()?.toFloatOrNull() ?: 150f
                val id = "geo_${UUID.randomUUID()}"

                viewModel.addGeofenceArea(id, name, latLng.latitude, latLng.longitude, radius)
                try {
                    GeofenceHelper(requireContext()).addGeofence(
                        GeofenceHelper(requireContext()).createGeofence(id, latLng.latitude, latLng.longitude, radius)
                    )
                    Snackbar.make(binding.root, "Geofence created: $name", Snackbar.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Snackbar.make(binding.root, "Failed to create geofence", Snackbar.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

