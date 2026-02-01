package com.travelcompanion.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.livedata.observeAsState
import com.travelcompanion.ui.map.MapViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import com.travelcompanion.ui.map.MapManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    tripId: Long?,
    viewModel: MapViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    // Configure OSMDroid once for this composable
    LaunchedEffect(Unit) {
        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
        Configuration.getInstance().userAgentValue = context.packageName
        // Ensure ViewModel has loaded data
        viewModel.loadJourneysForMap()
        viewModel.loadGeofenceAreas()
    }

    val journeys by viewModel.journeys.observeAsState(emptyList())
    val geofences by viewModel.geofenceAreas.observeAsState(emptyList())

    Scaffold(
        topBar = { TopAppBar(title = { Text("Mappa Percorso") }) }
    ) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .fillMaxSize()) {

            if (tripId != null) {
                Text("Visualizzazione Trip ID: $tripId", modifier = Modifier.padding(16.dp))

                // MapView embedded inside Compose
                AndroidView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    factory = { ctx ->
                        MapView(ctx).apply {
                            setTileSource(TileSourceFactory.MAPNIK)
                            setMultiTouchControls(true)
                        }
                    },
                    update = { mapView ->
                        // Clear previous overlays
                        MapManager.clearPolylines(mapView)
                        MapManager.clearMarkers(mapView)
                        MapManager.clearGeofences(mapView)

                        // Draw journeys for this trip
                        val points = journeys
                            .filter { it.tripId == tripId }
                            .flatMap { j ->
                                j.coordinates.map { coord -> GeoPoint(coord.latitude, coord.longitude) }
                            }

                        if (points.size >= 2) {
                            MapManager.drawPolyline(mapView, points, android.graphics.Color.parseColor("#4285F4"), 8f)
                            MapManager.centerMap(mapView, points.first(), 12.0)
                        } else if (points.size == 1) {
                            MapManager.centerMap(mapView, points.first(), 14.0)
                        }

                        // Draw geofence areas (if any)
                        geofences.forEach { area ->
                            val center = GeoPoint(area.latitude, area.longitude)
                            MapManager.addGeofenceCircle(mapView, center, area.radiusMeters.toDouble())
                        }

                        mapView.invalidate()
                    }
                )

            } else {
                Text("Nessun viaggio selezionato", modifier = Modifier.padding(16.dp))
            }
        }
    }
}
