package com.travelcompanion.data.db.converters

import androidx.room.TypeConverter
import com.travelcompanion.domain.model.TripType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromTripType(tripType: TripType): String {
        return tripType.name
    }

    @TypeConverter
    fun toTripType(name: String): TripType {
        return TripType.valueOf(name)
    }

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromCoordinatesJson(json: String?): List<com.travelcompanion.domain.model.Coordinate> {
        json ?: return emptyList()
        val type = object : TypeToken<List<com.travelcompanion.domain.model.Coordinate>>() {}.type
        return gson.fromJson(json, type)
    }

    @TypeConverter
    fun coordinatesToJson(coordinates: List<com.travelcompanion.domain.model.Coordinate>): String {
        return gson.toJson(coordinates)
    }
}

