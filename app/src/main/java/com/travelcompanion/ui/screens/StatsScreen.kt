package com.travelcompanion.ui.screens

import android.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.livedata.observeAsState
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.travelcompanion.ui.statistics.StatisticsViewModel
import com.travelcompanion.domain.model.MonthlyStat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(viewModel: StatisticsViewModel = hiltViewModel()) {
    val monthlyStats by viewModel.monthlyStats.observeAsState(emptyList())

    Scaffold(
        topBar = { TopAppBar(title = { Text("Statistiche di Viaggio") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text("Analisi Mensile", style = MaterialTheme.typography.headlineSmall)

            Spacer(modifier = Modifier.height(16.dp))

            // Grafico MPAndroidChart
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                factory = { context ->
                    BarChart(context).apply {
                        description.isEnabled = false
                        setNoDataText("Nessun dato disponibile")
                        setFitBars(true)
                        setBackgroundColor(Color.TRANSPARENT)
                        axisRight.isEnabled = false
                        xAxis.textColor = Color.DKGRAY
                        axisLeft.textColor = Color.DKGRAY
                    }
                },
                update = { chart ->
                    val entries = ArrayList<BarEntry>()
                    // monthlyStats is a list of MonthlyStat with month 1..12
                    val stats = if (monthlyStats.isNotEmpty()) monthlyStats else defaultDummyStats()
                    stats.forEachIndexed { idx, ms ->
                        entries.add(BarEntry(idx.toFloat(), ms.totalDistance))
                    }

                    val dataSet = BarDataSet(entries, "Km Percorsi").apply {
                        color = Color.parseColor("#6750A4")
                        valueTextColor = Color.DKGRAY
                    }

                    val data = BarData(dataSet)
                    data.barWidth = 0.9f
                    chart.data = data
                    chart.invalidate()
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Riquadro previsione se presente (StatisticsViewModel non fornisce prediction; placeholder)
            Text("Previsione AI", style = MaterialTheme.typography.titleMedium)
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Nessuna previsione disponibile al momento.")
                }
            }
        }
    }
}

private fun defaultDummyStats(): List<MonthlyStat> {
    // Returns 12 months of dummy stats
    return (1..12).map { m ->
        MonthlyStat(month = m, tripCount = (0..5).random(), totalDistance = (0..200).random().toFloat(), totalDuration = 0L)
    }
}
