package com.example.travelcompanion.data.local

import androidx.room.TypeConverter
import com.example.travelcompanion.domain.model.TripType

class Converters {
    @TypeConverter
    fun fromTripType(value: TripType): String = value.name

    @TypeConverter
    fun toTripType(value: String): TripType = TripType.valueOf(value)
}

