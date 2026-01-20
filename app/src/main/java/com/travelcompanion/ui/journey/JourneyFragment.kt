package com.travelcompanion.ui.journey

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.travelcompanion.databinding.FragmentJourneyBinding

class JourneyFragment : Fragment() {
    private var _binding: FragmentJourneyBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentJourneyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // For now show static/demo values until data layer wiring is completed
        binding.tvDestination.text = "Sample Destination"
        binding.tvDates.text = "15-20 Jan 2026"
        binding.tvDistance.text = "Distance: 0 km"
        binding.tvDuration.text = "Duration: 0h 0m"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

