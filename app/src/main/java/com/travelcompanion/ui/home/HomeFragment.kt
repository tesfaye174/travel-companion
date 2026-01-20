package com.travelcompanion.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.travelcompanion.databinding.FragmentHomeBinding
import com.travelcompanion.R

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

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

        setupClickListeners()
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

