package com.travelcompanion.ui.map

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import timber.log.Timber
import dagger.hilt.android.AndroidEntryPoint
import com.travelcompanion.location.LocationProvider
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import com.travelcompanion.databinding.FragmentMapBinding
import com.travelcompanion.utils.PermissionUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import android.content.pm.PackageManager
import com.google.android.material.snackbar.Snackbar
import java.text.DateFormat
import java.util.Date
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import com.travelcompanion.utils.PaletteUtils
import androidx.preference.PreferenceManager
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider

@AndroidEntryPoint
class MapFragment : Fragment() {
    // Mostra un messaggio di errore nella UI
    private fun showMapError(message: String) {
        view?.let {
            Snackbar.make(it, message, Snackbar.LENGTH_LONG).show()
        }
    }

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

        // Diagnostic logging: which fragment and which ViewModel provider factories are present
        try {
            Timber.d("this=${this::class.java.name} defaultFactory=${defaultViewModelProviderFactory::class.java.name} activityFactory=${requireActivity().defaultViewModelProviderFactory::class.java.name}")
        } catch (t: Throwable) {
            Timber.d(t, "failed to read factories")
        }

        setupListeners()

        mapView = binding.mapContainer

        // Required for OSMDroid to work
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        Configuration.getInstance().load(requireContext(), sharedPreferences)
        Configuration.getInstance().userAgentValue = requireContext().packageName
        mapView?.setTileSource(TileSourceFactory.MAPNIK)
        mapView?.setMultiTouchControls(true)

        // Load custom OSM data from assets/map.osm if available and valid
        loadOsmData()

        viewModel.loadJourneysForMap()
        viewModel.loadGeofenceAreas()
        viewModel.loadGeofenceEvents()

        observeAndRender()
    }

    private fun loadOsmData() {
        try {
            val inputStream = requireContext().assets.open("map.osm")
            val factory = XmlPullParserFactory.newInstance()
            val parser = factory.newPullParser()
            parser.setInput(inputStream, null)

            var eventType = parser.eventType
            val nodes = mutableListOf<GeoPoint>()
            var hasValidData = false

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && parser.name == "node") {
                    val lat = parser.getAttributeValue(null, "lat")?.toDoubleOrNull()
                    val lon = parser.getAttributeValue(null, "lon")?.toDoubleOrNull()
                    if (lat != null && lon != null) {
                        nodes.add(GeoPoint(lat, lon))
                        hasValidData = true
                    }
                }
                eventType = parser.next()
            }

            if (hasValidData) {
                // Add markers for nodes
                nodes.forEach { point ->
                    val marker = Marker(mapView)
                    marker.position = point
                    marker.title = "OSM Node"
                    mapView?.overlays?.add(marker)
                }
                Timber.d("Loaded ${nodes.size} OSM nodes")
            } else {
                Timber.w("No valid OSM data found in map.osm. The file may be incomplete or corrupted.")
                showMapError("Nessun dato valido trovato in map.osm. Il file potrebbe essere mancante o corrotto.")
            }
        } catch (e: java.io.IOException) {
            Timber.e(e, "Errore di I/O durante il caricamento OSM: ${e.message}")
            showMapError("File map.osm non trovato nella cartella assets.")
        } catch (e: org.xmlpull.v1.XmlPullParserException) {
            Timber.e(e, "Errore di parsing XML OSM: ${e.message}")
            showMapError("Errore di parsing del file map.osm.")
        } catch (e: Exception) {
            Timber.e(e, "Failed to load OSM data: ${e.message}")
            showMapError("Errore sconosciuto durante il caricamento della mappa.")
        }
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
        } else {
            showMapError("Nessun viaggio o percorso disponibile da mostrare sulla mappa.")
        }
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

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        locationProvider.getCurrentLocation({ location ->
            map.overlays.removeIf { it is MyLocationNewOverlay }
            val myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), map)
            myLocationOverlay.enableMyLocation()
            map.overlays.add(myLocationOverlay)

            val latLng = GeoPoint(location.latitude, location.longitude)
            map.controller.setCenter(latLng)
            map.controller.setZoom(14.0)
        }, { _ ->
            Snackbar.make(binding.root, "Unable to obtain current location", Snackbar.LENGTH_SHORT).show()
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
        _binding = null
    }
}
