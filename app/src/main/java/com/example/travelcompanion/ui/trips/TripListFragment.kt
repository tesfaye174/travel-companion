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

        val adapter = TripAdapter { trip ->
            // Handle trip click
        }
        binding.recyclerViewTrips.adapter = adapter

        viewModel.allTrips.observe(viewLifecycleOwner) { trips ->
            adapter.submitList(trips)
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
