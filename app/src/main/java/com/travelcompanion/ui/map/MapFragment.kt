package com.travelcompanion.ui.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.travelcompanion.R
import com.travelcompanion.databinding.FragmentMapBinding
import com.travelcompanion.location.LocationProvider
import com.travelcompanion.utils.PermissionUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.text.DateFormat
import java.util.Date
import javax.inject.Inject

@AndroidEntryPoint
class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MapViewModel by viewModels()
    private var mapView: MapView? = null
    private var myLocationOverlay: MyLocationNewOverlay? = null

    @Inject
    lateinit var locationProvider: LocationProvider

    private var showHeatmap = false
    private var hasSetInitialCenter = false

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

        val toolbar = binding.root.findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        (requireActivity() as? androidx.appcompat.app.AppCompatActivity)?.apply {
            setSupportActionBar(toolbar)
            supportActionBar?.title = getString(R.string.map)
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
        }

        setupListeners()

        mapView = binding.mapContainer
        // osmdroid Configuration is initialized once in TravelCompanionApplication.onCreate()
        mapView?.setTileSource(TileSourceFactory.MAPNIK)
        mapView?.setMultiTouchControls(true)

        setupMyLocationOverlay()
        setupLongPressForGeofence()

        viewModel.loadJourneysForMap()
        viewModel.loadGeofenceAreas()
        viewModel.loadGeofenceEvents()

        observeAndRender()
    }

    private fun setupMyLocationOverlay() {
        val map = mapView ?: return
        val ctx = context ?: return
        if (PermissionUtils.hasLocationPermissions(ctx)) {
            val overlay = MyLocationNewOverlay(GpsMyLocationProvider(ctx), map)
            overlay.enableMyLocation()
            // Auto-center on first fix so the map doesn't sit on Rome fallback for users elsewhere
            overlay.runOnFirstFix {
                map.post {
                    if (!hasSetInitialCenter && overlay.myLocation != null) {
                        map.controller.setZoom(14.0)
                        map.controller.setCenter(overlay.myLocation)
                        hasSetInitialCenter = true
                    }
                }
            }
            map.overlays.add(overlay)
            myLocationOverlay = overlay
        }
    }

    private fun setupLongPressForGeofence() {
        val map = mapView ?: return
        val receiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?) = false
            override fun longPressHelper(p: GeoPoint?): Boolean {
                if (p == null) return false
                promptAddGeofence(p)
                return true
            }
        }
        // Insert events overlay at the bottom so it doesn't steal taps from FABs/markers
        map.overlays.add(0, MapEventsOverlay(receiver))
    }

    private fun promptAddGeofence(point: GeoPoint) {
        val ctx = context ?: return
        val input = android.widget.EditText(ctx).apply {
            hint = getString(R.string.geofence_name_hint)
            setSingleLine(true)
        }
        MaterialAlertDialogBuilder(ctx)
            .setTitle(R.string.add_geofence_title)
            .setView(input)
            .setPositiveButton(R.string.add) { _, _ ->
                val name = input.text?.toString()?.trim().orEmpty().ifBlank { getString(R.string.geofence_default_name) }
                val id = "geofence_${System.currentTimeMillis()}"
                viewModel.addGeofenceArea(id, name, point.latitude, point.longitude, 100f)
                Snackbar.make(binding.root, getString(R.string.geofence_added, name), Snackbar.LENGTH_SHORT).show()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    @OptIn(FlowPreview::class)
    private fun observeAndRender() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Coalesce rapid journey/geofence updates into one refresh per 150ms
                launch {
                    combine(viewModel.journeys, viewModel.geofenceAreas) { _, _ -> Unit }
                        .debounce(150)
                        .collect { refreshMap() }
                }
                // Don't follow the user's location after the first fix — they may have panned away.
                launch {
                    viewModel.currentLocation.collect { geoPoint ->
                        if (geoPoint != null && !hasSetInitialCenter) {
                            mapView?.controller?.setZoom(14.0)
                            mapView?.controller?.setCenter(geoPoint)
                            hasSetInitialCenter = true
                        }
                    }
                }
            }
        }
    }

    private fun refreshMap() {
        val map = mapView ?: return
        // Remove all drawn overlays but keep the user location dot
        map.overlays.removeAll { it !is MyLocationNewOverlay }

        val journeys = viewModel.journeys.value
        // Group coordinates by trip so each route is rendered as its own polyline
        // (avoids fake "teleport" segments between unrelated trips)
        val journeysByTrip = journeys
            .filter { it.coordinates.isNotEmpty() }
            .groupBy { it.tripId }

        val allPoints = journeys.flatMap { j ->
            j.coordinates.map { GeoPoint(it.latitude, it.longitude) }
        }

        // Center map on first load only (don't recenter on heatmap toggle)
        if (!hasSetInitialCenter) {
            when {
                allPoints.size >= 2 -> fitMapToPoints(map, allPoints)
                allPoints.size == 1 -> MapManager.centerMap(map, allPoints.first(), 14.0)
                else -> centerOnDefaultLocation()
            }
            hasSetInitialCenter = true
        }

        // Draw one polyline per trip, cycling through a color palette so adjacent
        // trips are visually distinguishable
        journeysByTrip.entries.forEachIndexed { idx, (tripId, tripJourneys) ->
            val tripPoints = tripJourneys
                .sortedBy { it.startTime }
                .flatMap { j -> j.coordinates.map { GeoPoint(it.latitude, it.longitude) } }
            if (tripPoints.size >= 2) {
                val color = ROUTE_COLORS[idx % ROUTE_COLORS.size]
                MapManager.drawPolyline(map, tripPoints, color, 10f)
                // Start marker for the route
                MapManager.addMarker(map, tripPoints.first(), getString(R.string.trip_id_label, tripId))
            } else if (tripPoints.size == 1) {
                MapManager.addMarker(map, tripPoints.first(), getString(R.string.trip_id_label, tripId))
            }
        }

        // Draw heatmap if enabled
        if (showHeatmap && allPoints.isNotEmpty()) {
            MapManager.drawHeatmap(map, allPoints)
        }

        // Draw geofence circles
        viewModel.geofenceAreas.value.forEach { area ->
            MapManager.addGeofenceCircle(
                map,
                GeoPoint(area.latitude, area.longitude),
                area.radiusMeters.toDouble()
            )
        }

        map.invalidate()
    }

    private fun fitMapToPoints(map: MapView, points: List<GeoPoint>) {
        val bbox = BoundingBox.fromGeoPoints(points)
        // Post so the map has measured itself before we ask it to zoom-to-bounds
        map.post { map.zoomToBoundingBox(bbox, true, 80) }
    }

    companion object {
        // Distinct, color-blind friendly palette for distinguishing trip routes
        private val ROUTE_COLORS = intArrayOf(
            0xFF2563EB.toInt(), // blue
            0xFFEA580C.toInt(), // orange
            0xFF16A34A.toInt(), // green
            0xFFDC2626.toInt(), // red
            0xFF9333EA.toInt(), // purple
            0xFF0891B2.toInt(), // teal
            0xFFCA8A04.toInt(), // amber
            0xFFDB2777.toInt()  // pink
        )
    }

    private fun centerOnDefaultLocation() {
        val map = mapView ?: return
        val overlay = myLocationOverlay
        if (overlay?.myLocation != null) {
            MapManager.centerMap(map, overlay.myLocation, 10.0)
        } else {
            // Default to Rome (central to existing trip data)
            MapManager.centerMap(map, GeoPoint(41.9028, 12.4964), 5.0)
        }
    }

    private fun setupListeners() {
        binding.fabMyLocation.setOnClickListener {
            centerOnMyLocation()
        }

        binding.fabHeatmap.setOnClickListener {
            showHeatmap = !showHeatmap
            refreshMap()
        }

        binding.fabGeofenceEvents.setOnClickListener {
            showGeofenceEventsDialog()
        }
    }

    private fun showGeofenceEventsDialog() {
        val context = context ?: return
        val events = viewModel.geofenceEvents.value
        if (events.isEmpty()) {
            Snackbar.make(binding.root, getString(R.string.no_geofence_events), Snackbar.LENGTH_SHORT).show()
            return
        }

        val areaNameById = viewModel.geofenceAreas.value.associateBy({ it.id }, { it.name })
        val df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)

        val items = events.take(50).map { e ->
            val name = areaNameById[e.geofenceId] ?: e.geofenceId
            val whenText = df.format(Date(e.timestamp))
            "${e.transition} • $name • $whenText"
        }.toTypedArray()

        MaterialAlertDialogBuilder(context)
            .setTitle(getString(R.string.geofence_events))
            .setItems(items, null)
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }

    private fun centerOnMyLocation() {
        val context = context ?: return
        if (!PermissionUtils.hasLocationPermissions(context)) {
            Snackbar.make(binding.root, getString(R.string.location_permission_required), Snackbar.LENGTH_LONG).show()
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                2001
            )
            return
        }
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        locationProvider.getCurrentLocation({ location ->
            viewModel.updateCurrentLocation(location.latitude, location.longitude)
        }, { _ ->
            Snackbar.make(binding.root, getString(R.string.unable_to_get_location), Snackbar.LENGTH_SHORT).show()
        })
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
        myLocationOverlay?.enableMyLocation()
    }

    override fun onPause() {
        super.onPause()
        myLocationOverlay?.disableMyLocation()
        mapView?.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        myLocationOverlay?.disableMyLocation()
        myLocationOverlay = null
        mapView?.onDetach()
        mapView = null
        hasSetInitialCenter = false
        _binding = null
    }
}
