package com.example.travelcompanion.ui.tripdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelcompanion.R
import com.example.travelcompanion.TravelCompanionApplication
import com.example.travelcompanion.databinding.FragmentTripDetailsBinding
import com.example.travelcompanion.utils.DateUtils
import com.example.travelcompanion.utils.LocationUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TripDetailsFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentTripDetailsBinding? = null
    private val binding get() = _binding!!

    private val args: TripDetailsFragmentArgs by navArgs()

    private val viewModel: TripDetailsViewModel by viewModels {
        TripDetailsViewModelFactory(
            (requireActivity().application as TravelCompanionApplication).repository,
            args.tripId
        )
    }

    private var googleMap: GoogleMap? = null
    private lateinit var photosAdapter: PhotosAdapter
    private lateinit var notesAdapter: NotesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTripDetailsBinding.inflate(inflater, container, false)
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPhotosRecyclerView()
        setupNotesRecyclerView()
        setupClickListeners()
        observeTripDetails()
    }

    private fun setupPhotosRecyclerView() {
        photosAdapter = PhotosAdapter { photo ->
            // Apri visualizzatore foto
            // TODO: Implementa PhotoViewerFragment o Dialog
        }

        binding.recyclerPhotos.apply {
            layoutManager = GridLayoutManager(context, 3) // 3 colonne
            adapter = photosAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupNotesRecyclerView() {
        notesAdapter = NotesAdapter(
            onNoteClick = { note ->
                // Apri editor nota
                // TODO: Implementa NoteEditorDialog
            },
            onDeleteClick = { note ->
                viewModel.deleteNote(note)
            }
        )

        binding.recyclerNotes.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = notesAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupClickListeners() {
        binding.buttonStartTracking.setOnClickListener {
            viewModel.startTracking()
            findNavController().navigate(R.id.action_tripDetails_to_journey)
        }

        binding.buttonAddPhoto.setOnClickListener {
            // Apri camera per scattare foto
            openCamera()
        }

        binding.buttonAddNote.setOnClickListener {
            // Apri dialog per aggiungere nota
            showAddNoteDialog()
        }

        binding.buttonViewAllPhotos.setOnClickListener {
            // Naviga alla galleria completa
            // TODO: Implementa PhotoGalleryFragment
        }

        binding.buttonViewAllNotes.setOnClickListener {
            // Espandi/comprimi lista note
            toggleNotesVisibility()
        }
    }

    private fun observeTripDetails() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.tripDetails.collectLatest { details ->
                details?.let { updateUI(it) }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.photos.collectLatest { photos ->
                photosAdapter.submitList(photos)
                binding.textPhotoCount.text = "${photos.size}"

                // Mostra/nascondi sezione foto
                if (photos.isEmpty()) {
                    binding.sectionPhotos.visibility = View.GONE
                } else {
                    binding.sectionPhotos.visibility = View.VISIBLE
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.notes.collectLatest { notes ->
                notesAdapter.submitList(notes)
                binding.textNoteCount.text = "${notes.size}"

                // Mostra/nascondi sezione note
                if (notes.isEmpty()) {
                    binding.sectionNotes.visibility = View.GONE
                } else {
                    binding.sectionNotes.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun updateUI(details: com.example.travelcompanion.domain.model.TripDetails) {
        with(binding) {
            // Informazioni base
            textTripTitle.text = details.trip.title
            textDestination.text = details.trip.destination
            textTripType.text = details.trip.tripType.name.replace("_", " ")

            // Date
            textStartDate.text = DateUtils.formatDate(details.trip.startDate)
            textEndDate.text = DateUtils.formatDate(details.trip.endDate)

            // Durata
            val days = DateUtils.getDaysDifference(details.trip.startDate, details.trip.endDate)
            textTotalDays.text = "$days giorni"

            // Statistiche
            textTotalDistance.text = LocationUtils.formatDistance(details.totalDistance)
            textTotalDuration.text = LocationUtils.formatDuration(details.totalDuration)

            // Numero di journeys
            textJourneyCount.text = "${details.journeys.size}"

            // Disegna percorso sulla mappa
            if (details.locationPoints.isNotEmpty()) {
                drawRouteOnMap(details.locationPoints)
            }
        }
    }

    private fun drawRouteOnMap(points: List<com.example.travelcompanion.domain.model.LocationPoint>) {
        if (points.isEmpty() || googleMap == null) return

        val latLngs = points.map { LatLng(it.latitude, it.longitude) }

        googleMap?.apply {
            clear() // Pulisce markers e polyline precedenti

            // Disegna la polyline (percorso)
            val polylineOptions = PolylineOptions()
                .addAll(latLngs)
                .color(android.graphics.Color.parseColor("#2196F3"))
                .width(10f)
            addPolyline(polylineOptions)

            // Aggiungi marker di inizio e fine
            if (latLngs.isNotEmpty()) {
                // Marker inizio (verde)
                addMarker(
                    MarkerOptions()
                        .position(latLngs.first())
                        .title("Inizio")
                        .icon(com.google.android.gms.maps.model.BitmapDescriptorFactory
                            .defaultMarker(com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_GREEN))
                )

                // Marker fine (rosso)
                addMarker(
                    MarkerOptions()
                        .position(latLngs.last())
                        .title("Fine")
                        .icon(com.google.android.gms.maps.model.BitmapDescriptorFactory
                            .defaultMarker(com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_RED))
                )

                // Centra la camera sul percorso
                val boundsBuilder = LatLngBounds.Builder()
                latLngs.forEach { boundsBuilder.include(it) }
                val bounds = boundsBuilder.build()
                val padding = 100 // padding in pixels

                try {
                    animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))
                } catch (e: Exception) {
                    // Se la mappa non è ancora pronta, usa moveCamera
                    moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))
                }
            }
        }
    }

    private fun openCamera() {
        // TODO: Implementa apertura camera
        // Puoi usare CameraHelper già creato o un intent per la camera
        /*
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        }
        */
    }

    private fun showAddNoteDialog() {
        // TODO: Implementa dialog per aggiungere nota
        // Puoi creare un DialogFragment personalizzato
        /*
        AddNoteDialog.newInstance(args.tripId).apply {
            setOnNoteSavedListener { title, content ->
                viewModel.addNote(title, content)
            }
            show(childFragmentManager, "AddNoteDialog")
        }
        */
    }

    private fun toggleNotesVisibility() {
        if (binding.recyclerNotes.visibility == View.VISIBLE) {
            binding.recyclerNotes.visibility = View.GONE
            binding.buttonViewAllNotes.text = "Mostra tutte le note"
        } else {
            binding.recyclerNotes.visibility = View.VISIBLE
            binding.buttonViewAllNotes.text = "Nascondi note"
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Configurazione mappa
        googleMap?.apply {
            uiSettings.isZoomControlsEnabled = true
            uiSettings.isCompassEnabled = true
            uiSettings.isMyLocationButtonEnabled = false
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

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
    }
}