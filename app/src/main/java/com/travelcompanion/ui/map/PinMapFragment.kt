package com.travelcompanion.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.travelcompanion.R
import com.travelcompanion.databinding.FragmentPinMapBinding
import com.travelcompanion.location.LocationProvider
import dagger.hilt.android.AndroidEntryPoint
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject

/**
 * Fragment che mostra una mappa interattiva dove l'utente può toccare
 * per posizionare un pin con icona personalizzata.
 * Il pin si sposta ogni volta che l'utente tocca un nuovo punto.
 */
@AndroidEntryPoint
class PinMapFragment : Fragment() {

    private var _binding: FragmentPinMapBinding? = null
    private val binding get() = _binding!!

    private var mapView: MapView? = null
    private var currentMarker: Marker? = null
    private var customPinIcon: Drawable? = null

    @Inject
    lateinit var locationProvider: LocationProvider

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPinMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configura OSMDroid
        Configuration.getInstance().load(
            requireContext(),
            requireContext().getSharedPreferences("osmdroid", android.content.Context.MODE_PRIVATE)
        )
        Configuration.getInstance().userAgentValue = requireContext().packageName

        // Carica l'icona personalizzata per il pin
        customPinIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_custom_pin)

        setupMap()
        setupListeners()
    }

    private fun setupMap() {
        mapView = binding.mapView
        mapView?.apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)

            // Centro iniziale su Roma, Italia
            val defaultCenter = GeoPoint(41.9028, 12.4964)
            controller.setZoom(10.0)
            controller.setCenter(defaultCenter)

            // Aggiungi l'overlay per gestire i tap sulla mappa
            val mapEventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
                override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                    p?.let { geoPoint ->
                        placePin(geoPoint)
                    }
                    return true
                }

                override fun longPressHelper(p: GeoPoint?): Boolean {
                    // Non gestire long press
                    return false
                }
            })
            overlays.add(0, mapEventsOverlay)
        }
    }

    /**
     * Posiziona o sposta il pin sulla mappa al punto specificato.
     */
    private fun placePin(geoPoint: GeoPoint) {
        val map = mapView ?: return

        // Se esiste già un marker, rimuovilo
        currentMarker?.let { marker ->
            map.overlays.remove(marker)
        }

        // Crea un nuovo marker con icona personalizzata
        currentMarker = Marker(map).apply {
            position = geoPoint
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            icon = customPinIcon
            title = getString(R.string.pin_marker_title)
            snippet = formatCoordinates(geoPoint)
        }

        map.overlays.add(currentMarker)
        map.invalidate()

        // Mostra le coordinate nella card
        showCoordinates(geoPoint)

        Timber.d("Pin posizionato a: ${geoPoint.latitude}, ${geoPoint.longitude}")
    }

    /**
     * Mostra le coordinate del pin nella card informativa.
     */
    private fun showCoordinates(geoPoint: GeoPoint) {
        binding.tvCoordinates.apply {
            visibility = View.VISIBLE
            text = formatCoordinates(geoPoint)
        }
        binding.tvHint.text = getString(R.string.pin_map_hint_placed)
    }

    /**
     * Formatta le coordinate in formato leggibile.
     */
    private fun formatCoordinates(geoPoint: GeoPoint): String {
        return String.format(
            Locale.getDefault(),
            "Lat: %.6f, Lon: %.6f",
            geoPoint.latitude,
            geoPoint.longitude
        )
    }

    private fun setupListeners() {
        binding.fabMyLocation.setOnClickListener {
            centerOnMyLocation()
        }
    }

    private fun centerOnMyLocation() {
        val context = context ?: return

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Snackbar.make(
                binding.root,
                R.string.location_permission_required,
                Snackbar.LENGTH_LONG
            ).setAction(R.string.ok) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }.show()
            return
        }

        Snackbar.make(binding.root, R.string.loading, Snackbar.LENGTH_SHORT).show()

        locationProvider.getCurrentLocation({ location ->
            val geoPoint = GeoPoint(location.latitude, location.longitude)
            mapView?.controller?.animateTo(geoPoint)
            mapView?.controller?.setZoom(15.0)
        }, { error ->
            Snackbar.make(
                binding.root,
                R.string.error_getting_location,
                Snackbar.LENGTH_LONG
            ).show()
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
        _binding = null
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 3001
    }
}
