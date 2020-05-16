package dev.amin.echangecenter.core.utils

import dev.amin.echangecenter.R
import java.util.*


object CurrencyHelper {

    // [CurrencyCode] = (CurrencyName, CurrencySymbol)
    private val currencies = hashMapOf<String, Pair<String, String>>()

    init {

        /* First I get all the locales then I try to get a currency from them,
            Some of the locales are not associated with a currency. I could also filter
            the array and search on a dictionary but I believe this is faster */
        val locales = Locale.getAvailableLocales()

        locales.forEach {

            try {
                val currency = Currency.getInstance(it)
                currencies[currency.currencyCode] = Pair(currency.displayName, currency.symbol)
            } catch (e: Exception) {
                // Just pass it's ok
            }
        }
    }

    /***
     *
     * We have icons thanks to this repo :)
     *
     * https://github.com/transferwise/currency-flags
     */
    private val icons = hashMapOf(
        "AUD" to R.drawable.ic_aud, "BGN" to R.drawable.ic_bgn,
        "BRL" to R.drawable.ic_brl, "CAD" to R.drawable.ic_cad,
        "CHF" to R.drawable.ic_chf, "CNY" to R.drawable.ic_cny,
        "CZK" to R.drawable.ic_czk, "DKK" to R.drawable.ic_dkk,
        "GBP" to R.drawable.ic_gbp, "HKD" to R.drawable.ic_hkd,
        "HRK" to R.drawable.ic_hrk, "HUF" to R.drawable.ic_huf,
        "IDR" to R.drawable.ic_idr, "ILS" to R.drawable.ic_ils,
        "INR" to R.drawable.ic_inr, "ISK" to R.drawable.ic_isk,
        "JPY" to R.drawable.ic_jpy, "KRW" to R.drawable.ic_krw,
        "MXN" to R.drawable.ic_mxn, "MYR" to R.drawable.ic_myr,
        "NOK" to R.drawable.ic_nok, "NZD" to R.drawable.ic_nzd,
        "PHP" to R.drawable.ic_php, "PLN" to R.drawable.ic_pln,
        "RON" to R.drawable.ic_ron, "RUB" to R.drawable.ic_rub,
        "SEK" to R.drawable.ic_sek, "THB" to R.drawable.ic_thb,
        "USD" to R.drawable.ic_usd, "ZAR" to R.drawable.ic_zar,
        "SGD" to R.drawable.ic_sgd, "EUR" to R.drawable.ic_eur
    )

    fun getIcon(currencyCode: String): Int {
        return icons[currencyCode] ?: -1
    }

    /***
     * @param currencyCode e.g EUR
     * @return Pair<String, String> (CurrencyName, CurrencySymbol)
     */
    fun getCurrencyInfo(currencyCode: String): Pair<String, String> {
        return currencies[currencyCode] ?: Pair("", "")
    }
}
