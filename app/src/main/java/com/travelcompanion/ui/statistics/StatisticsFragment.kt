package com.travelcompanion.ui.statistics

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.travelcompanion.R
import com.travelcompanion.databinding.FragmentStatisticsBinding
import com.travelcompanion.domain.model.MonthlyStat
import com.travelcompanion.domain.model.TripTypeStat
import com.travelcompanion.domain.usecase.PredictionResult
import com.travelcompanion.utils.DateUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.DateFormatSymbols
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
        setupPeriodChips()
        observeViewModel()
    }

    private fun setupPeriodChips() {
        val initialChipId = when (viewModel.initialPeriod) {
            StatPeriod.THIS_MONTH -> R.id.chip_this_month
            StatPeriod.ALL_TIME -> R.id.chip_all_time
            StatPeriod.THIS_YEAR -> R.id.chip_this_year
        }
        binding.chipGroupPeriod.check(initialChipId)
        binding.chipGroupPeriod.setOnCheckedStateChangeListener { group, _ ->
            viewModel.setPeriod(
                when (group.checkedChipId) {
                    R.id.chip_this_month -> StatPeriod.THIS_MONTH
                    R.id.chip_all_time -> StatPeriod.ALL_TIME
                    else -> StatPeriod.THIS_YEAR
                }
            )
        }
    }

    private fun setupCharts() {
        binding.chartBar.apply {
            description.isEnabled = false
            setDrawGridBackground(false)
            setDrawBarShadow(false)
            legend.isEnabled = false
            axisRight.isEnabled = false
            axisLeft.axisMinimum = 0f
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
            }
            animateY(1000)
        }
        binding.chartPie.apply {
            description.isEnabled = false
            setDrawHoleEnabled(true)
            holeRadius = 40f
            transparentCircleRadius = 45f
            setEntryLabelColor(Color.BLACK)
            setEntryLabelTextSize(12f)
            legend.isEnabled = true
            animateY(1000)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.totalTrips.collect { binding.tvTotalTrips.text = it.toString() } }
                launch { viewModel.totalDistance.collect { binding.tvTotalDistance.text = String.format(Locale.getDefault(), "%.2f km", it) } }
                launch { viewModel.totalPhotos.collect { binding.tvTotalPhotos.text = it.toString() } }
                launch { viewModel.totalDuration.collect { binding.tvTotalDuration.text = DateUtils.formatDuration(it) } }
                launch { viewModel.tripTypeStats.collect { updatePieChart(it) } }
                launch { viewModel.monthlyStats.collect { updateBarChart(it) } }
                launch { viewModel.prediction.collect { result -> result?.let { updatePredictionCard(it) } } }
            }
        }
    }

    private fun updatePredictionCard(result: PredictionResult) {
        if (result.predictedKm <= 0.0) {
            binding.tvPredictionKm.text = getString(R.string.prediction_no_data)
            binding.tvPredictionMessage.visibility = View.GONE
        } else {
            binding.tvPredictionKm.text = getString(R.string.prediction_km_format, result.predictedKm)
            binding.tvPredictionMessage.text = result.message
            binding.tvPredictionMessage.visibility = View.VISIBLE
        }
    }

    private fun updateBarChart(stats: List<MonthlyStat>) {
        val entries = stats.mapIndexed { i, s -> BarEntry(i.toFloat(), s.tripCount.toFloat()) }
        binding.chartBar.data = BarData(
            BarDataSet(entries, "").apply {
                color = Color.parseColor("#3F51B5")
                valueTextColor = Color.BLACK
            }
        ).apply { barWidth = 0.9f }

        val shortMonths = DateFormatSymbols.getInstance(Locale.getDefault()).shortMonths
        binding.chartBar.xAxis.valueFormatter = IndexAxisValueFormatter(
            stats.map { stat -> shortMonths.getOrNull(stat.month - 1) ?: stat.month.toString() }.toTypedArray()
        )
        binding.chartBar.setFitBars(true)
        binding.chartBar.invalidate()
    }

    private fun updatePieChart(stats: List<TripTypeStat>) {
        val entries = stats
            .filter { it.tripCount > 0 }
            .map { PieEntry(it.tripCount.toFloat(), it.tripType.name.replace('_', ' ')) }

        binding.chartPie.data = PieData(
            PieDataSet(entries, "").apply {
                colors = listOf(
                    Color.parseColor("#4CAF50"),
                    Color.parseColor("#FF9800"),
                    Color.parseColor("#2196F3"),
                    Color.parseColor("#9C27B0")
                )
                valueTextColor = Color.BLACK
                valueTextSize = 12f
            }
        )
        binding.chartPie.invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
