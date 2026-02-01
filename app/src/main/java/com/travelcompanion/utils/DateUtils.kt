package com.travelcompanion.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * Utility object per la formattazione delle date nell'app.
 *
 * NOTA IMPORTANTE SULLA THREAD SAFETY:
 * SimpleDateFormat NON è thread-safe. Tuttavia, in questa app lo uso
 * principalmente dal main thread (UI), quindi non dovrebbero esserci problemi.
 *
 * Se in futuro usassi questi formatter da thread multipli (es. in coroutine
 * su Dispatchers.IO), dovrei:
 * - Creare un nuovo formatter ogni volta, oppure
 * - Usare ThreadLocal, oppure
 * - Migrare a java.time.format.DateTimeFormatter (API 26+ o con desugaring)
 *
 * Per ora ho scelto di riutilizzare le istanze per efficienza.
 */
object DateUtils {

    // Formatter riutilizzabili - inizializzati una sola volta
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val dateTimeFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())

    // ==================== FORMATTAZIONE DATE ====================

    /** Formatta un timestamp Unix in stringa leggibile (es. "15 Gen 2024") */
    fun formatDate(timestamp: Long): String {
        return dateFormat.format(Date(timestamp))
    }

    /** Overload che accetta direttamente un oggetto Date */
    fun formatDate(date: Date): String {
        return dateFormat.format(date)
    }

    /** Formatta solo l'ora (es. "14:30") */
    fun formatTime(timestamp: Long): String {
        return timeFormat.format(Date(timestamp))
    }

    /** Formatta data e ora insieme (es. "15 Gen 2024, 14:30") */
    fun formatDateTime(timestamp: Long): String {
        return dateTimeFormat.format(Date(timestamp))
    }

    fun formatDateTime(date: Date): String {
        return dateTimeFormat.format(date)
    }

    // ==================== RANGE DI DATE ====================

    /**
     * Formatta un intervallo di date per la visualizzazione.
     * Es: "15 Gen 2024 - 20 Gen 2024"
     */
    fun formatDateRange(startDate: Long, endDate: Long): String {
        val start = formatDate(startDate)
        val end = formatDate(endDate)
        return "$start - $end"
    }

    /**
     * Versione che gestisce anche date di fine nulle (viaggio in corso).
     * Se endDate è null, mostra solo la data di inizio.
     */
    fun formatDateRange(start: Date, end: Date?): String {
        return if (end == null) {
            formatDate(start)
        } else {
            "${formatDate(start)} - ${formatDate(end)}"
        }
    }

    // ==================== CALCOLI ====================

    /**
     * Calcola il numero di giorni tra due date (inclusi).
     * Es: dal 1 al 3 Gennaio = 3 giorni
     */
    fun getDaysDifference(startDate: Long, endDate: Long): Int {
        val diff = endDate - startDate
        return (diff / (1000 * 60 * 60 * 24)).toInt() + 1
    }

    /**
     * Formatta una durata in millisecondi in formato leggibile.
     * Es: 5400000 ms -> "1h 30m"
     *
     * Gestisce i casi limite:
     * - Solo ore se i minuti sono 0 (es. "2h")
     * - Solo minuti se meno di un'ora (es. "45m")
     * - "0m" per durate nulle o negative
     */
    fun formatDuration(durationMillis: Long): String {
        if (durationMillis <= 0) return "0m"

        val totalMinutes = (durationMillis / 1000 / 60).toInt()
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60

        return if (hours > 0) {
            if (minutes > 0) "${hours}h ${minutes}m" else "${hours}h"
        } else {
            "${minutes}m"
        }
    }

    /** Overload di getDaysDifference che accetta oggetti Date */
    fun getDaysDifference(start: Date, end: Date): Int {
        val diff = end.time - start.time
        return (diff / (1000 * 60 * 60 * 24)).toInt() + 1
    }
}
