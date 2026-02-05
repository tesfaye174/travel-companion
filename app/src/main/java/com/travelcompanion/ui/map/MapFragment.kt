package com.travelcompanion.ui.map

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import com.travelcompanion.location.LocationProvider
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import com.travelcompanion.databinding.FragmentMapBinding
import com.travelcompanion.R
import com.travelcompanion.utils.PermissionUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import android.content.pm.PackageManager
import com.google.android.material.snackbar.Snackbar
import java.text.DateFormat
import java.util.Date
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import com.travelcompanion.utils.PaletteUtils
import android.content.Context
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import timber.log.Timber

@AndroidEntryPoint
class MapFragment : Fragment() {

    // ActivityResult launcher per i permessi di localizzazione
    private lateinit var locationPermissionLauncher: ActivityResultLauncher<Array<String>>

    // Nota: per mostrare errori UI usiamo Snackbar direttamente con binding.root

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    // Use fragment-scoped ViewModel so Hilt's fragment factory provides the correct ViewModelProvider
    private val viewModel: MapViewModel by viewModels()
    private var mapView: MapView? = null

    @javax.inject.Inject
    lateinit var locationProvider: LocationProvider

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

        // Inizializzo il permission launcher per richiedere fine+coarse
        locationPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val granted = permissions.entries.all { it.value }
            if (granted) {
                // Quando concesso, centriamo la mappa
                centerOnMyLocation()
            } else {
                val denied = permissions.filter { !it.value }.map { it.key }
                val shouldShowRationale = denied.any { permission ->
                    ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), permission)
                }
                if (shouldShowRationale) {
                    PermissionUtils.showPermissionRationaleDialog(
                        requireContext(),
                        getString(R.string.location_permission_required),
                        getString(R.string.location_permission_rationale),
                        onConfirm = { locationPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)) },
                        onCancel = {}
                    )
                } else {
                    // Utente ha negato senza rationale o ha disabilitato permanentemente: apri settings
                    PermissionUtils.showSettingsDialog(requireContext())
                }
            }
        }

        // Diagnostic logging: which fragment and which ViewModel provider factories are present
        try {
            Timber.d("this=%s defaultFactory=%s activityFactory=%s", this::class.java.name, defaultViewModelProviderFactory::class.java.name, requireActivity().defaultViewModelProviderFactory::class.java.name)
        } catch (t: Throwable) {
            Timber.d(t, "failed to read factories")
        }

        setupListeners()

        mapView = binding.mapContainer

        // Required for OSMDroid to work
        val ctx = requireContext()
        val sharedPreferences = ctx.getSharedPreferences("osmdroid", Context.MODE_PRIVATE)
        Configuration.getInstance().load(ctx, sharedPreferences)
        Configuration.getInstance().userAgentValue = ctx.packageName

        // Setup map with online tiles (MAPNIK from OpenStreetMap)
        mapView?.setTileSource(TileSourceFactory.MAPNIK)
        mapView?.setMultiTouchControls(true)

        // Set default center to Italy (can be overridden by user location or trip data)
        val defaultCenter = GeoPoint(41.9028, 12.4964) // Rome, Italy
        mapView?.controller?.setZoom(6.0)
        mapView?.controller?.setCenter(defaultCenter)

        // Load custom POI markers from assets if available (optional)
        loadCustomPOIMarkers()

        viewModel.loadJourneysForMap()
        viewModel.loadGeofenceAreas()
        viewModel.loadGeofenceEvents()

        observeAndRender()
    }

    /**
     * Loads custom Points of Interest from assets/poi.osm if available.
     * This is optional - the map will work with online tiles even without this file.
     */
    private fun loadCustomPOIMarkers() {
        try {
            val inputStream = requireContext().assets.open("poi.osm")
            val factory = XmlPullParserFactory.newInstance()
            val parser = factory.newPullParser()
            parser.setInput(inputStream, null)

            var eventType = parser.eventType
            val pois = mutableListOf<POIData>()

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && parser.name == "node") {
                    val lat = parser.getAttributeValue(null, "lat")?.toDoubleOrNull()
                    val lon = parser.getAttributeValue(null, "lon")?.toDoubleOrNull()
                    val name = parser.getAttributeValue(null, "name") ?: "POI"
                    if (lat != null && lon != null) {
                        pois.add(POIData(GeoPoint(lat, lon), name))
                    }
                }
                eventType = parser.next()
            }
            inputStream.close()

            if (pois.isNotEmpty()) {
                pois.forEach { poi ->
                    val marker = Marker(mapView)
                    marker.position = poi.location
                    marker.title = poi.name
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    mapView?.overlays?.add(marker)
                }
                Timber.d("Loaded ${pois.size} custom POI markers")
            }
        } catch (fnf: java.io.FileNotFoundException) {
            // No custom POI file - this is fine, map works without it
            Timber.d(fnf, "No custom POI file found (poi.osm) - using online map tiles only")
        } catch (e: java.io.IOException) {
            Timber.w(e, "Could not read POI file: ${e.message}")
        } catch (e: Exception) {
            Timber.w(e, "Error loading custom POIs: ${e.message}")
        }
    }

    private data class POIData(val location: GeoPoint, val name: String)

    private fun observeAndRender() {
        viewModel.journeys.observe(viewLifecycleOwner) { journeys ->
            renderJourneys(journeys)
        }
        viewModel.geofenceAreas.observe(viewLifecycleOwner) { areas ->
            renderGeofences(areas)
        }
    }

    private fun renderJourneys(journeys: List<com.travelcompanion.domain.model.Journey>) {
        val map = mapView ?: return
        MapManager.clearPolylines(map)
        MapManager.clearMarkers(map)
        val allPoints = journeys.flatMap { j -> j.coordinates.map { GeoPoint(it.latitude, it.longitude) } }
        if (allPoints.size >= 2) {
            MapManager.drawPolyline(map, allPoints, PaletteUtils.greenLight(requireContext()), 8f)
            MapManager.centerMap(map, allPoints.first(), 10.0)
            if (showRoutePoints) {
                allPoints.forEach { p ->
                    MapManager.addMarker(map, p, "Trip")
                }
            }
        } else if (allPoints.size == 1) {
            MapManager.centerMap(map, allPoints.first(), 14.0)
        }
        // No error message if empty - it's normal to have no trips yet
    }

    private fun renderGeofences(areas: List<com.travelcompanion.domain.model.GeofenceArea>) {
        val map = mapView ?: return
        MapManager.clearGeofences(map)
        areas.forEach { area ->
            val center = GeoPoint(area.latitude, area.longitude)
            MapManager.addGeofenceCircle(map, center, area.radiusMeters.toDouble())
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
        val map = mapView ?: return
        val ctx = context ?: return

        if (!PermissionUtils.hasLocationPermissions(ctx)) {
            // Use modern ActivityResult API to request permissions
            locationPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
            return
        }

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission check failed, do nothing (already requested above)
            return
        }

        // Mostra indicatore di caricamento
        Snackbar.make(binding.root, getString(R.string.getting_location), Snackbar.LENGTH_SHORT).show()

        locationProvider.getCurrentLocation({ location ->
            try {
                // Remove existing MyLocationNewOverlay (compatible with API 21+)
                val iterator = map.overlays.iterator()
                while (iterator.hasNext()) {
                    if (iterator.next() is MyLocationNewOverlay) {
                        iterator.remove()
                    }
                }
                val myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(ctx), map)
                myLocationOverlay.enableMyLocation()
                map.overlays.add(myLocationOverlay)

                val latLng = GeoPoint(location.latitude, location.longitude)
                map.controller.animateTo(latLng)
                map.controller.setZoom(15.0)
                map.invalidate()
            } catch (e: Exception) {
                Timber.w(e, "Error centering on user location")
            }
        }, { error ->
            Snackbar.make(binding.root, getString(R.string.unable_get_location), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.retry)) { centerOnMyLocation() }
                .show()
            Timber.w(error, "Failed to get current location")
        })
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView?.onDetach()
        mapView = null
        _binding = null
    }
}
