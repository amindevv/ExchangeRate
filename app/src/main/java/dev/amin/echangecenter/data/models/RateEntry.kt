package dev.amin.echangecenter.data.models

class RateEntry(

    val currency: String = "",
    val exchangeRate: Double = 0.0,

    /* This is just a UI feature, may
        someone prefers to hide some pairs */
    val isVisible: Boolean = true
)