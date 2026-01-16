package com.example.travelcompanion.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.travelcompanion.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry

class StatisticsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        // Initialize BarChart
        val barChart = findViewById<BarChart>(R.id.bar_chart)
        val data = BarData(getBarDataSet())
        barChart.data = data
        barChart.invalidate()
    }

    // Implement getBarDataSet
    private fun getBarDataSet(): BarDataSet {
        val entries = listOf(
            BarEntry(1f, 5f),
            BarEntry(2f, 3f),
            BarEntry(3f, 8f)
        )
        return BarDataSet(entries, "Trips per Month")
    }
}