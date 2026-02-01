package com.travelcompanion.data.db.converters

import androidx.room.TypeConverter
import com.travelcompanion.domain.model.TripType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

/**
 * Type converters per Room Database.
 *
 * Room non sa come salvare direttamente oggetti complessi come Date, enum
 * o liste di oggetti custom. Devo definire converter che trasformano
 * questi tipi in tipi primitivi che SQLite può gestire (String, Long, ecc.).
 *
 * I converter sono registrati nel database con @TypeConverters(Converters::class)
 *
 * @see <a href="https://developer.android.com/training/data-storage/room/referencing-data">Room Type Converters</a>
 */
class Converters {
    // Gson per serializzare/deserializzare le liste di coordinate in JSON
    private val gson = Gson()

    // ==================== CONVERTER PER TripType ====================
    // Salvo l'enum come stringa (il nome del valore enum)

    @TypeConverter
    fun fromTripType(tripType: TripType): String {
        return tripType.name
    }

    @TypeConverter
    fun toTripType(name: String): TripType {
        return TripType.valueOf(name)
    }

    // ==================== CONVERTER PER Date ====================
    // Salvo la data come timestamp Long (millisecondi da epoch)

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    // ==================== CONVERTER PER Lista Coordinate ====================
    // Le coordinate del percorso GPS sono salvate come JSON string.
    // Ho scelto JSON invece di una tabella separata per semplicità,
    // dato che le coordinate sono sempre lette/scritte insieme al Journey.

    @TypeConverter
    fun fromCoordinatesJson(json: String?): List<com.travelcompanion.domain.model.Coordinate> {
        json ?: return emptyList()
        // TypeToken è necessario per preservare il tipo generico a runtime
        val type = object : TypeToken<List<com.travelcompanion.domain.model.Coordinate>>() {}.type
        return gson.fromJson(json, type)
    }

    @TypeConverter
    fun coordinatesToJson(coordinates: List<com.travelcompanion.domain.model.Coordinate>): String {
        return gson.toJson(coordinates)
    }
}
