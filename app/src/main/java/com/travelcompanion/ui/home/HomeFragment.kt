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
import com.travelcompanion.ui.trips.TripsAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var destinationsAdapter: DestinationsAdapter
    private lateinit var tripsAdapter: TripsAdapter

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
        setupTripsRecyclerView()
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

    private fun setupTripsRecyclerView() {
        tripsAdapter = TripsAdapter(
            onTripClick = { trip ->
                val bundle = Bundle().apply {
                    putLong("tripId", trip.id)
                }
                findNavController().navigate(R.id.navigation_trip_details, bundle)
            },
            onTripLongClick = { _ -> }
        )

        binding.rvRecentTrips.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = tripsAdapter
        }
    }

    private fun setupClickListeners() {
        binding.fabAddTrip.setOnClickListener {
            findNavController().navigate(R.id.navigation_new_trip)
        }

        binding.btnStartTrip.setOnClickListener {
            findNavController().navigate(R.id.navigation_new_trip)
        }

        binding.btnMyTrips.setOnClickListener {
            findNavController().navigate(R.id.navigation_trips)
        }

        binding.btnExplore.setOnClickListener {
            findNavController().navigate(R.id.navigation_map)
        }
    }

    private fun observeViewModel() {
        viewModel.quickStats.observe(viewLifecycleOwner) { stats ->
            binding.tvTotalTrips.text = getString(R.string.total_trips_val, stats.totalTrips)
            binding.tvTotalDistance.text = getString(R.string.total_distance_val, stats.totalDistance)
            binding.layoutQuickStats.visibility = if (stats.totalTrips > 0) View.VISIBLE else View.GONE
        }

        viewModel.recentTrips.observe(viewLifecycleOwner) { trips ->
            if (trips.isEmpty()) {
                binding.rvRecentTrips.visibility = View.GONE
                binding.layoutEmptyState.root.visibility = View.VISIBLE
            } else {
                binding.rvRecentTrips.visibility = View.VISIBLE
                binding.layoutEmptyState.root.visibility = View.GONE
                tripsAdapter.submitList(trips)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

