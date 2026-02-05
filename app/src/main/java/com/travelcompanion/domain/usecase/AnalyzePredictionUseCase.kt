package com.travelcompanion.domain.usecase

import com.travelcompanion.domain.model.LocationPoint
import com.travelcompanion.domain.model.Trip

data class PredictionResult(
    val predictedKm: Double,
    val message: String
)

class AnalyzePredictionUseCase {
    fun execute(trips: List<Trip>, locations: List<LocationPoint>): PredictionResult {
        if (trips.isEmpty()) return PredictionResult(0.0, "Non ci sono dati sufficienti.")

        val totalKm = calculateTotalDistance(locations)
        val avgKmPerTrip = if (trips.isNotEmpty()) totalKm / trips.size else 0.0

        // Algoritmo di previsione semplice: si calcola la media dei chilometri percorsi per viaggio e si applica un coefficiente di ottimismo (1.2).
        // Questo serve a simulare una previsione personalizzata, come richiesto dal progetto, senza introdurre complessità statistica o machine learning.
        // In una futura estensione si potrebbero usare modelli più avanzati (es. regressione, reti neurali) per una stima più accurata.
        val predicted = avgKmPerTrip * 1.2

        // Messaggio motivazionale: se la previsione supera 100 km, si valorizza il comportamento dell'utente.
        // Altrimenti si mostra una stima personalizzata per il prossimo periodo.
        val msg = if (predicted > 100) "Sei un viaggiatore instancabile!"
        else "Il prossimo mese potresti fare ${String.format(java.util.Locale.getDefault(), "%.1f", predicted)} km."

        return PredictionResult(predicted, msg)
    }

    private fun calculateTotalDistance(locations: List<LocationPoint>): Double {
        if (locations.size < 2) return 0.0
        var dist = 0.0
        for (i in 0 until locations.size - 1) {
            dist += haversine(
                locations[i].latitude, locations[i].longitude,
                locations[i + 1].latitude, locations[i + 1].longitude
            )
        }
        return dist
    }

    private fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0 // Earth radius in km

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return r * c
    }
}
