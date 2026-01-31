package com.travelcompanion.data.local

/**
 * Questo file esiste per compatibilità con riferimenti legacy a
 * `com.travelcompanion.data.local.TripDatabase`.
 *
 * La implementazione principale si trova in `com.travelcompanion.data.db.AppDatabase`.
 * Qui forniamo un alias per mantenere la compatibilità e ridurre duplicazioni.
 */

@Deprecated("Usa com.travelcompanion.data.db.AppDatabase al posto di questo alias")
typealias TripDatabase = com.travelcompanion.data.db.AppDatabase
