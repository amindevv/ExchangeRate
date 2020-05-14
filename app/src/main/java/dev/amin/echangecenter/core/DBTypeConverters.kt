package dev.amin.echangecenter.core

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dev.amin.echangecenter.data.models.RateEntry
import dev.amin.echangecenter.data.models.Rates
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

    @TypeConverter // Jsonify the RateEntry List
    fun stringToRatesList(value: String): List<RateEntry> {

        val type = object : TypeToken<List<RateEntry>>() {}.type

        return Gson().fromJson(value, type)
    }

    @TypeConverter // UnJsonify the RateEntryList
    fun ratesListToString(list: List<RateEntry>): String {

        return Gson().toJson(list)
    }
}