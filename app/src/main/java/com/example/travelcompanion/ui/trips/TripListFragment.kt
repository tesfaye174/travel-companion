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
            adapter.submitList(trips)
        }

        // Setup filter destination
        binding.editFilterDestination.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                viewModel.setFilterDestination(s?.toString() ?: "")
            }
        })

        // Setup filter type dropdown
        val typeItems = arrayOf("All Types", "Local", "Day", "Multi-day")
        val typeAdapter = android.widget.ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            typeItems
        )
        binding.spinnerFilterType.setAdapter(typeAdapter)
        binding.spinnerFilterType.setText("All Types", false)
        binding.spinnerFilterType.setOnItemClickListener { _, _, position, _ ->
            val type = when (position) {
                0 -> null
                1 -> com.example.travelcompanion.domain.model.TripType.LOCAL
                2 -> com.example.travelcompanion.domain.model.TripType.DAY
                3 -> com.example.travelcompanion.domain.model.TripType.MULTI_DAY
                else -> null
            }
            viewModel.setFilterType(type)
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
