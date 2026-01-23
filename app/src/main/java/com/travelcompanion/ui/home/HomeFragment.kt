package com.travelcompanion.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.travelcompanion.databinding.FragmentHomeBinding
import com.travelcompanion.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var destinationsAdapter: DestinationsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDestinationsRecyclerView()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupDestinationsRecyclerView() {
        destinationsAdapter = DestinationsAdapter { destination ->
            // Navigate to new trip with pre-filled destination
            val bundle = Bundle().apply {
                putString("destination", "${destination.city}, ${destination.country}")
            }
            findNavController().navigate(R.id.navigation_new_trip, bundle)
        }

        binding.rvDestinations.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = destinationsAdapter
        }

        // Load suggested destinations
        destinationsAdapter.submitList(SuggestedDestinations.destinations)
    }

    private fun setupClickListeners() {
        binding.btnNewTrip.setOnClickListener {
            findNavController().navigate(R.id.navigation_new_trip)
        }

        binding.btnViewTrips.setOnClickListener {
            findNavController().navigate(R.id.navigation_trips)
        }

        binding.btnViewMap.setOnClickListener {
            findNavController().navigate(R.id.navigation_map)
        }

        binding.btnViewStats.setOnClickListener {
            findNavController().navigate(R.id.navigation_statistics)
        }

        binding.fabAddTrip.setOnClickListener {
            findNavController().navigate(R.id.navigation_new_trip)
        }
    }

    private fun observeViewModel() {
        viewModel.quickStats.observe(viewLifecycleOwner) { stats ->
            binding.tvTotalTrips.text = getString(R.string.total_trips_val, stats.totalTrips)
            binding.tvTotalDistance.text = getString(R.string.total_distance_val, stats.totalDistance)
        }

        viewModel.recentTrips.observe(viewLifecycleOwner) { trips ->
            if (trips.isEmpty()) {
                binding.rvRecentTrips.visibility = View.GONE
                binding.layoutEmptyState.root.visibility = View.VISIBLE
            } else {
                binding.rvRecentTrips.visibility = View.VISIBLE
                binding.layoutEmptyState.root.visibility = View.GONE
                // Here you would normally update the adapter
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

