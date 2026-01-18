package com.travelcompanion.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.travelcompanion.databinding.FragmentMapBinding

class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMap()
        setupListeners()
    }

    private fun setupMap() {
        // Initialize map
    }

    private fun setupListeners() {
        binding.fabMyLocation.setOnClickListener {
            // Center map on user location
        }

        binding.fabHeatmap.setOnClickListener {
            // Toggle heatmap view
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}