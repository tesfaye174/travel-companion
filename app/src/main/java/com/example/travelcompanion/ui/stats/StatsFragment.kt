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
            val entries = stats.map { 
                BarEntry(it.month.toFloat(), it.distance.toFloat())
            }
            val dataSet = BarDataSet(entries, "Distance (km)")
            binding.barChart.data = BarData(dataSet)
            binding.barChart.invalidate()
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
