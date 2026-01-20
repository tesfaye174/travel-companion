package com.travelcompanion.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.travelcompanion.databinding.FragmentStatisticsBinding

class StatsFragment : Fragment() {
    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Static placeholders until statistics wiring is implemented
        binding.tvTotalTrips.text = "0"
        binding.tvTotalDistance.text = "0 km"
        binding.tvTotalPhotos.text = "0"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

