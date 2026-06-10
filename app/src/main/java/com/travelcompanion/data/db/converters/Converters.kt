package com.travelcompanion.data.db.converters

import androidx.room.TypeConverter
import com.travelcompanion.domain.model.Coordinate
import com.travelcompanion.domain.model.TripType
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.util.Date

class Converters {
    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val coordinateListAdapter = moshi.adapter<List<CoordinateJson>>(
        Types.newParameterizedType(List::class.java, CoordinateJson::class.java)
    )

    @TypeConverter
    fun fromTripType(tripType: TripType): String {
        return tripType.name
    }

    @TypeConverter
    fun toTripType(name: String): TripType {
        return try {
            TripType.valueOf(name)
        } catch (e: IllegalArgumentException) {
            TripType.OTHER
        }
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
    fun fromCoordinatesJson(json: String?): List<Coordinate> {
        json ?: return emptyList()
        return try {
            coordinateListAdapter.fromJson(json)?.map {
                Coordinate(it.latitude, it.longitude, Date(it.timestamp))
            } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    @TypeConverter
    fun coordinatesToJson(coordinates: List<Coordinate>): String {
        val jsonList = coordinates.map {
            CoordinateJson(it.latitude, it.longitude, it.timestamp.time)
        }
        return coordinateListAdapter.toJson(jsonList)
    }

    private data class CoordinateJson(
        val latitude: Double,
        val longitude: Double,
        val timestamp: Long
    )
}

