package dev.amin.echangecenter.data.models

class RatesResponse(

    val baseCurrency: String = "",
    val rates: HashMap<String, Double> = hashMapOf()
)