package dev.amin.echangecenter.core

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class DBTypeConverters {

    @TypeConverter // Date to Long
    fun toDate(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter // Long to Date
    fun toLong(value: Date?): Long? {
        return value?.time
    }

    @TypeConverter // Jsonify the maps
    fun stringToRatesMap(value: String): HashMap<String, Double> {

        val type = object : TypeToken<HashMap<String, Double>>() {}.type

        return Gson().fromJson(value, type)
    }

    @TypeConverter // UnJsonify the maps
    fun toString(value: HashMap<String, Double>): String {
        return Gson().toJson(value)
    }
}