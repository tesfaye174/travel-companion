package com.example.travelcompanion.ui.trips

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.travelcompanion.R
import com.example.travelcompanion.TravelCompanionApplication
import com.example.travelcompanion.databinding.FragmentTripListBinding

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.travelcompanion.ui.trips.TripViewModel

class TripListFragment : Fragment() {
    private var _binding: FragmentTripListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TripViewModel by viewModels {
        TripViewModelFactory((requireActivity().application as TravelCompanionApplication).repository)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTripListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = TripAdapter { _ ->
            // Handle trip click - could navigate to trip details
        }
        binding.recyclerViewTrips.adapter = adapter

        viewModel.allTrips.observe(viewLifecycleOwner) { trips ->
            if (trips.isEmpty()) {
                binding.recyclerViewTrips.visibility = View.GONE
                binding.layoutEmptyState.visibility = View.VISIBLE
            } else {
                binding.recyclerViewTrips.visibility = View.VISIBLE
                binding.layoutEmptyState.visibility = View.GONE
                adapter.submitList(trips)
            }
        }

        // Setup ChipGroup filter
        binding.chipGroupFilters.setOnCheckedChangeListener { _, checkedId ->
            val type = when (checkedId) {
                R.id.chip_all -> null
                R.id.chip_local -> com.example.travelcompanion.domain.model.TripType.LOCAL
                R.id.chip_day -> com.example.travelcompanion.domain.model.TripType.DAY
                R.id.chip_multi -> com.example.travelcompanion.domain.model.TripType.MULTI_DAY
                else -> null
            }
            viewModel.setFilterType(type)
        }

        binding.btnSearch.setOnClickListener {
             // Future: Implement search dialog or expand
             com.google.android.material.snackbar.Snackbar.make(view, "Search functionality coming soon", com.google.android.material.snackbar.Snackbar.LENGTH_SHORT).show()
        }

        binding.fabAddTrip.setOnClickListener {
            findNavController().navigate(R.id.action_trips_to_add)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
