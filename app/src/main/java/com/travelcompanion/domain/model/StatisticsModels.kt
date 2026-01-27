package com.travelcompanion.domain.model

import androidx.room.ColumnInfo

/**
 * Statistica mensile per i viaggi: mese, numero viaggi, distanza e durata totali.
 */
data class MonthlyStat(
	@ColumnInfo(name = "month")
	val month: Int,
	@ColumnInfo(name = "tripCount")
	val tripCount: Int,
	@ColumnInfo(name = "totalDistance")
	val totalDistance: Float,
	@ColumnInfo(name = "totalDuration")
	val totalDuration: Long
)

/**
 * Statistica per tipo di viaggio: tipo, conteggio e percentuale.
 */
data class TripTypeStat(
	@ColumnInfo(name = "type")
	val type: String,
	@ColumnInfo(name = "count")
	val count: Int,
	@ColumnInfo(name = "percentage")
	val percentage: Float
)
