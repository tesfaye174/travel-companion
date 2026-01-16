package com.example.travelcompanion.ui.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.travelcompanion.databinding.FragmentStatsBinding

import androidx.fragment.app.viewModels
import com.example.travelcompanion.TravelCompanionApplication
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry

import com.example.travelcompanion.R

class StatsFragment : Fragment() {
    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StatsViewModel by viewModels {
        StatsViewModelFactory((requireActivity().application as TravelCompanionApplication).repository)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.tripStats.observe(viewLifecycleOwner) { stats ->
            // Calculate Totals safely
            val totalTrips = stats.sumOf { it.count }
            val totalDist = stats.sumOf { it.distance }
            val totalDuration = 0 // Placeholder, add duration to MonthlyStat if needed
            
            binding.textTotalTrips.text = totalTrips.toString()
            binding.textTotalDistance.text = String.format("%.0f km", totalDist)
            binding.textTotalDuration.text = String.format("%.0f hrs", totalDuration.toDouble())

            val entries = stats.map { 
                BarEntry(it.month.toFloat(), it.distance.toFloat())
            }
            if (entries.isNotEmpty()) {
                val dataSet = BarDataSet(entries, "Distance (km)")
                dataSet.color = requireContext().getColor(R.color.md_theme_light_primary)
                binding.barChart.data = BarData(dataSet)
                binding.barChart.invalidate()
            }
        }

        viewModel.forecast.observe(viewLifecycleOwner) { forecast ->
            binding.textForecast.text = "Predicted: ${forecast.predictedCount} trips, ${String.format("%.1f", forecast.predictedDistance)} km"
            binding.textSuggestion.text = "Suggestion: ${forecast.suggestion}"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
