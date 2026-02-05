package com.travelcompanion.utils

import java.text.SimpleDateFormat
import java.util.*
import timber.log.Timber

/**
 * Oggetto di utilità per gestire le date nell'app.
 *
 * Attenzione: SimpleDateFormat non è thread-safe! Qui lo uso solo nella UI, quindi va bene.
 * Se dovessi usarlo in thread diversi, meglio crearne uno nuovo ogni volta o usare DateTimeFormatter.
 * Per ora va bene così, tanto serve solo per mostrare le date all'utente.
 */
object DateUtils {
    // Nota per lo studente: SimpleDateFormat NON è thread-safe.
    // Per evitare problemi (soprattutto con background thread) creo
    // un formatter nuovo in ogni funzione. In progetti più grandi
    // si preferisce usare java.time (DateTimeFormatter) o ThreadLocal.

    // --- Funzioni per formattare le date ---

    /** Converte un timestamp in una stringa tipo "15 Gen 2024" */
    fun formatDate(timestamp: Long): String {
        val df = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return df.format(Date(timestamp))
    }

    /** Stessa cosa ma prende direttamente una Date */
    fun formatDate(date: Date): String {
        val df = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return df.format(date)
    }

    /** Restituisce solo l'ora, tipo "14:30" */
    fun formatTime(timestamp: Long): String {
        val tf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return tf.format(Date(timestamp))
    }

    /** Data e ora insieme, tipo "15 Gen 2024, 14:30" */
    fun formatDateTime(timestamp: Long): String {
        val dtf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        return dtf.format(Date(timestamp))
    }

    fun formatDateTime(date: Date): String {
        val dtf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        return dtf.format(date)
    }

    /** Parso una stringa nel formato usato dall'app ("dd MMM yyyy"). */
    fun parseDate(text: String): Date? {
        return try {
            SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).parse(text)
        } catch (e: Exception) {
            Timber.w(e, "DateUtils: parseDate failed for '%s'", text)
            null
        }
    }

    /** Parso data+ora nel formato "dd MMM yyyy, HH:mm". */
    fun parseDateTime(text: String): Date? {
        return try {
            SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).parse(text)
        } catch (e: Exception) {
            Timber.w(e, "DateUtils: parseDateTime failed for '%s'", text)
            null
        }
    }

    // --- Funzioni per intervalli di date ---

    /**
     * Mostra un intervallo di date, tipo "15 Gen 2024 - 20 Gen 2024"
     */
    fun formatDateRange(startDate: Long, endDate: Long): String {
        val start = formatDate(startDate)
        val end = formatDate(endDate)
        return "$start - $end"
    }

    /**
     * Se la data di fine è nulla (viaggio in corso), mostra solo la data di inizio.
     */
    fun formatDateRange(start: Date, end: Date?): String {
        return if (end == null) {
            formatDate(start)
        } else {
            "${formatDate(start)} - ${formatDate(end)}"
        }
    }

    // --- Calcoli sulle date ---

    /**
     * Calcola quanti giorni ci sono tra due date (inclusi).
     * Es: dal 1 al 3 Gennaio = 3 giorni
     */
    fun getDaysDifference(startDate: Long, endDate: Long): Int {
        val diff = endDate - startDate
        return (diff / (1000 * 60 * 60 * 24)).toInt() + 1
    }

    /**
     * Trasforma una durata in millisecondi in una stringa leggibile.
     * Es: 5400000 ms -> "1h 30m"
     * Se la durata è zero o negativa, restituisce "0m"
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

    /** Versione con oggetti Date */
    fun getDaysDifference(start: Date, end: Date): Int {
        val diff = end.time - start.time
        return (diff / (1000 * 60 * 60 * 24)).toInt() + 1
    }

    // Nota: se cambi la lingua del telefono mentre l'app è aperta, i formatter non si aggiornano. In un'app seria bisognerebbe gestirlo meglio.
}
