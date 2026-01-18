package com.example.travelcompanion.utils

import android.content.Context
import android.graphics.Color
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*

object ChartUtils {

    fun setupBarChart(chart: BarChart, entries: List<BarEntry>, labels: List<String>, label: String) {
        val dataSet = BarDataSet(entries, label)
        dataSet.color = Color.parseColor("#2196F3")
        dataSet.valueTextColor = Color.BLACK
        dataSet.valueTextSize = 12f

        val data = BarData(dataSet)
        chart.data = data

        // Configure chart
        chart.description.isEnabled = false
        chart.legend.isEnabled = false
        chart.xAxis.valueFormatter = object : com.github.mikephil.charting.formatter.IndexAxisValueFormatter(labels) {}
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.xAxis.granularity = 1f
        chart.xAxis.labelCount = labels.size
        chart.axisLeft.axisMinimum = 0f
        chart.axisRight.isEnabled = false
        chart.setFitBars(true)

        chart.invalidate()
    }

    fun setupPieChart(chart: PieChart, entries: List<PieEntry>, colors: List<Int>) {
        val dataSet = PieDataSet(entries, "")
        dataSet.colors = colors
        dataSet.valueTextColor = Color.WHITE
        dataSet.valueTextSize = 14f

        val data = PieData(dataSet)
        chart.data = data

        // Configure chart
        chart.description.isEnabled = false
        chart.legend.isEnabled = true
        chart.setEntryLabelColor(Color.BLACK)
        chart.setEntryLabelTextSize(12f)
        chart.setHoleColor(Color.TRANSPARENT)
        chart.setTransparentCircleAlpha(0)

        chart.invalidate()
    }

    fun getTripTypeColors(): List<Int> {
        return listOf(
            Color.parseColor("#2196F3"), // Blue for Local
            Color.parseColor("#4CAF50"), // Green for Day Trip
            Color.parseColor("#F44336")  // Red for Multi-day
        )
    }
}