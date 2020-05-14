package dev.amin.echangecenter.core

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import dev.amin.echangecenter.data.models.RateEntry
import dev.amin.echangecenter.data.models.Rates
import java.lang.reflect.Type
import java.util.*

class RateGsonConverter : JsonDeserializer<Rates> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Rates {

        val jsonObject = json?.asJsonObject

        val baseCurrency = jsonObject?.get("baseCurrency")?.asString ?: ""

        val exchangeRatesList = getRatesList(jsonObject)

        return Rates(

            baseCurrency = baseCurrency,
            dateCreated = Date(System.currentTimeMillis()),

            rates = exchangeRatesList
        )
    }

    private fun getRatesList(jsonObject: JsonObject?): List<RateEntry> {

        val rateEntries = mutableListOf<RateEntry>()

        val exchangeRatesMap = jsonObject?.get("rates")?.asJsonObject ?: return emptyList()

        exchangeRatesMap.keySet().forEach { currency ->

            val rate = exchangeRatesMap[currency].asDouble

            rateEntries.add(
                RateEntry(
                    currency = currency,
                    exchangeRate = rate
                )
            )
        }

        return rateEntries
    }
}