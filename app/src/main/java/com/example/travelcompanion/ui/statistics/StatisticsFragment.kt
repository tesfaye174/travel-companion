package com.travelcompanion.ui.statistics

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.travelcompanion.databinding.FragmentStatisticsBinding

class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCharts()
        loadStatistics()
    }

    private fun setupCharts() {
        setupBarChart(binding.chartBar)
        setupPieChart(binding.chartPie)
    }

    private fun setupBarChart(chart: BarChart) {
        chart.description.isEnabled = false
        chart.setDrawGridBackground(false)
        chart.setDrawBarShadow(false)

        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f

        val leftAxis = chart.axisLeft
        leftAxis.setDrawGridLines(true)
        leftAxis.axisMinimum = 0f

        chart.axisRight.isEnabled = false
        chart.legend.isEnabled = false
        chart.animateY(1000)
    }

    private fun setupPieChart(chart: PieChart) {
        chart.description.isEnabled = false
        chart.setDrawHoleEnabled(true)
        chart.holeRadius = 40f
        chart.transparentCircleRadius = 45f
        chart.setEntryLabelColor(Color.BLACK)
        chart.setEntryLabelTextSize(12f)
        chart.legend.isEnabled = true
        chart.animateY(1000)
    }

    private fun loadStatistics() {
        // Load and display statistics
        binding.tvTotalTrips.text = "12"
        binding.tvTotalDistance.text = "450.2 km"
        binding.tvTotalPhotos.text = "48"

        updateBarChart()
        updatePieChart()
    }

    private fun updateBarChart() {
        // TODO: Implement bar chart data
    }

    private fun updatePieChart() {
        // TODO: Implement pie chart data
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}