package com.travelcompanion.ui.tripdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.travelcompanion.databinding.FragmentTripDetailsBinding

class TripDetailsFragment : Fragment() {

    private var _binding: FragmentTripDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var photoAdapter: PhotoAdapter
    private lateinit var noteAdapter: NoteAdapter

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
        loadTripDetails()
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

    private fun loadTripDetails() {
        // TODO: Load trip details from database
        binding.tvDestination.text = "Roma, Italia"
        binding.tvTripType.text = "Multi-giorno"
        binding.tvDates.text = "15-20 Gen 2026"
        binding.tvDistance.text = "Distanza: 120.5 km"
        binding.tvDuration.text = "Durata: 8h 2min"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}