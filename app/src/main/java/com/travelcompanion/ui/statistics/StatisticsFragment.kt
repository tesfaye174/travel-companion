package com.travelcompanion.ui.statistics

import android.graphics.Color
import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.data.*
import com.travelcompanion.domain.model.MonthlyStat
import com.travelcompanion.domain.model.TripTypeStat
import com.travelcompanion.databinding.FragmentStatisticsBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StatisticsViewModel by viewModels()

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
        observeViewModel()
        viewModel.loadStatistics()
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

    private fun observeViewModel() {
        viewModel.totalTrips.observe(viewLifecycleOwner) { count ->
            binding.tvTotalTrips.text = (count ?: 0).toString()
        }

        viewModel.totalDistance.observe(viewLifecycleOwner) { km ->
            binding.tvTotalDistance.text = "%.2f km".format(Locale.getDefault(), km ?: 0f)
        }

        viewModel.totalPhotos.observe(viewLifecycleOwner) { count ->
            binding.tvTotalPhotos.text = (count ?: 0).toString()
        }

        viewModel.totalDuration.observe(viewLifecycleOwner) { ms ->
            // Not currently displayed in layout, but useful for future UI
            ms ?: return@observe
        }

        viewModel.tripTypeStats.observe(viewLifecycleOwner) { stats ->
            updatePieChart(stats.orEmpty())
        }

        viewModel.monthlyStats.observe(viewLifecycleOwner) { stats ->
            updateBarChart(stats.orEmpty())
        }
    }

    private fun updateBarChart(stats: List<MonthlyStat>) {
        val entries = stats.mapIndexed { index, s ->
            BarEntry(index.toFloat(), s.tripCount.toFloat())
        }

        val dataSet = BarDataSet(entries, "Trips").apply {
            color = Color.parseColor("#3F51B5")
            valueTextColor = Color.BLACK
        }

        binding.chartBar.data = BarData(dataSet).apply {
            barWidth = 0.9f
        }

        val labels = stats.map { monthNumberToLabel(it.month) }
        binding.chartBar.xAxis.valueFormatter = IndexAxisValueFormatter(labels.toTypedArray())
        binding.chartBar.xAxis.position = XAxis.XAxisPosition.BOTTOM
        binding.chartBar.xAxis.granularity = 1f
        binding.chartBar.xAxis.setDrawGridLines(false)
        binding.chartBar.axisLeft.axisMinimum = 0f
        binding.chartBar.axisRight.isEnabled = false
        binding.chartBar.setFitBars(true)
        binding.chartBar.invalidate()
    }

    private fun updatePieChart(stats: List<TripTypeStat>) {
        val entries = stats
            .filter { it.count > 0 }
            .map { s ->
                PieEntry(s.count.toFloat(), s.type.replace('_', ' '))
            }

        val dataSet = PieDataSet(entries, "Trip types").apply {
            colors = listOf(
                Color.parseColor("#4CAF50"),
                Color.parseColor("#FF9800"),
                Color.parseColor("#2196F3"),
                Color.parseColor("#9C27B0")
            )
            valueTextColor = Color.BLACK
            valueTextSize = 12f
        }

        binding.chartPie.data = PieData(dataSet)
        binding.chartPie.invalidate()
    }

    private fun monthNumberToLabel(month: Int): String {
        return when (month) {
            1 -> "Jan"
            2 -> "Feb"
            3 -> "Mar"
            4 -> "Apr"
            5 -> "May"
            6 -> "Jun"
            7 -> "Jul"
            8 -> "Aug"
            9 -> "Sep"
            10 -> "Oct"
            11 -> "Nov"
            12 -> "Dec"
            else -> month.toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

