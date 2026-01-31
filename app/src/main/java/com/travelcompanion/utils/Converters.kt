package com.travelcompanion.utils

import androidx.room.TypeConverter
import com.travelcompanion.data.local.entity.TripType

class Converters {
    @TypeConverter
    fun fromTripType(type: TripType): String = type.name

    @TypeConverter
    fun toTripType(value: String): TripType = TripType.valueOf(value)
}
