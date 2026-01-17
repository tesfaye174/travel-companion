package com.example.travelcompanion.ui.statistics

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.travelcompanion.TravelCompanionApplication
import com.example.travelcompanion.databinding.FragmentStatisticsBinding
import com.example.travelcompanion.utils.LocationUtils
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StatisticsViewModel by viewModels {
        StatisticsViewModelFactory((requireActivity().application as TravelCompanionApplication).repository)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeStatistics()
    }

    private fun observeStatistics() {
        viewModel.statistics.observe(viewLifecycleOwner) { stats ->
            binding.textTotalTrips.text = "${stats.totalTrips}"
            binding.textTotalDistance.text = LocationUtils.formatDistance(stats.totalDistance)
            binding.textTotalDuration.text = "${stats.totalDuration / 3600000} hrs"

            setupBarChart(stats.tripsPerMonth)
        }
    }

    private fun setupBarChart(tripsPerMonth: Map<String, Int>) {
        val entries = mutableListOf<BarEntry>()
        val labels = mutableListOf<String>()

        val sortedEntries = tripsPerMonth.entries.sortedBy { it.key }
        sortedEntries.forEachIndexed { index, entry ->
            entries.add(BarEntry(index.toFloat(), entry.value.toFloat()))

            val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
            val monthIndex = entry.key.split("-")[1].toInt() - 1
            labels.add(monthNames[monthIndex])
        }

        val dataSet = BarDataSet(entries, "Trips per Month").apply {
            color = Color.parseColor("#2196F3")
            valueTextColor = Color.BLACK
            valueTextSize = 12f
        }

        val barData = BarData(dataSet)

        binding.chartTripsPerMonth.apply {
            data = barData
            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(labels)
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                setDrawGridLines(false)
            }
            axisLeft.apply {
                axisMinimum = 0f
                granularity = 1f
            }
            axisRight.isEnabled = false
            description.isEnabled = false
            legend.isEnabled = false
            animateY(1000)
            invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
