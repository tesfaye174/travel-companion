package com.travelcompanion.ui.tripdetails

import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.snackbar.Snackbar
import com.travelcompanion.databinding.FragmentTripDetailsBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class TripDetailsFragment : Fragment() {

    private var _binding: FragmentTripDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var photoAdapter: PhotoAdapter
    private lateinit var noteAdapter: NoteAdapter

    private val viewModel: TripDetailsViewModel by viewModels()
    private var googleMap: GoogleMap? = null
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private val dateTimeFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTripDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()
        setupMap()

        val tripId = arguments?.getLong("tripId", -1L) ?: -1L
        if (tripId <= 0L) {
            binding.tvDestination.text = "Trip non valido"
            binding.tvTripType.text = ""
            binding.tvDates.text = ""
            binding.tvDistance.text = ""
            binding.tvDuration.text = ""
            Snackbar.make(binding.root, "TripId mancante: apri un viaggio dalla lista", Snackbar.LENGTH_LONG).show()
            return
        }
        viewModel.setTripId(tripId)
        observeViewModel()
    }

    private fun setupMap() {
        val mapFragment = childFragmentManager.findFragmentById(com.travelcompanion.R.id.map_details) as? SupportMapFragment
        mapFragment?.getMapAsync(object : OnMapReadyCallback {
            override fun onMapReady(map: GoogleMap) {
                googleMap = map
                map.uiSettings.isZoomControlsEnabled = true
                renderRoute(viewModel.journeys.value.orEmpty())
            }
        })
    }

    private fun setupRecyclerViews() {
        photoAdapter = PhotoAdapter()
        binding.rvPhotos.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = photoAdapter
        }

        noteAdapter = NoteAdapter()
        binding.rvNotes.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = noteAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.trip.observe(viewLifecycleOwner) { trip ->
            if (trip == null) return@observe

            binding.tvDestination.text = trip.destination
            binding.tvTripType.text = trip.tripType.name.replace('_', ' ').lowercase().replaceFirstChar { it.titlecase() }

            val end = trip.endDate
            binding.tvDates.text = if (end != null) {
                "${dateFormat.format(trip.startDate)} - ${dateFormat.format(end)}"
            } else {
                dateFormat.format(trip.startDate)
            }
        }

        viewModel.totalDistanceKm.observe(viewLifecycleOwner) { km ->
            binding.tvDistance.text = "Distanza: %.2f km".format(km ?: 0.0)
        }

        viewModel.totalDurationMs.observe(viewLifecycleOwner) { ms ->
            val durationText = DateUtils.formatElapsedTime(((ms ?: 0L) / 1000L))
            binding.tvDuration.text = "Durata: $durationText"
        }

        viewModel.photoNotes.observe(viewLifecycleOwner) { photos ->
            val items = photos.orEmpty().map {
                PhotoAdapter.PhotoItem(
                    imageUrl = it.imagePath,
                    caption = it.note.ifBlank { "Foto" }
                )
            }
            photoAdapter.submitList(items)
        }

        viewModel.notes.observe(viewLifecycleOwner) { notes ->
            val items = notes.orEmpty().map {
                NoteAdapter.NoteItem(
                    content = it.content,
                    date = dateTimeFormat.format(it.timestamp)
                )
            }
            noteAdapter.submitList(items)
        }

        viewModel.journeys.observe(viewLifecycleOwner) { journeys ->
            renderRoute(journeys.orEmpty())
        }
    }

    private fun renderRoute(journeys: List<com.travelcompanion.domain.model.Journey>) {
        val map = googleMap ?: return
        map.clear()

        val allPoints = journeys.flatMap { j -> j.coordinates.map { LatLng(it.latitude, it.longitude) } }
        if (allPoints.size >= 2) {
            map.addPolyline(
                PolylineOptions()
                    .addAll(allPoints)
                    .width(8f)
            )
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(allPoints.first(), 12f))
        } else if (allPoints.size == 1) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(allPoints.first(), 14f))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

