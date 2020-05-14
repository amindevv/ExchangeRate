package dev.amin.echangecenter.data.repositories

import android.content.Context
import android.util.Log
import dev.amin.echangecenter.core.AppDb
import dev.amin.echangecenter.core.RetrofitClient
import dev.amin.echangecenter.data.RequestStatus
import dev.amin.echangecenter.data.models.Rates
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MainActivityRepository(context: Context) {

    private val db = AppDb.invoke(context)

    private var shouldReceiveUpdates = false

    /* Request Status is used for handling if the repo should
        make the next request or not */
    private var requestStatus = RequestStatus.NONE

    // Reference to the database, VM uses this
    val rates = db.ratesDao().getLiveRates()

    var baseCurrency = "EUR"

    private fun getExchangesRates() = runBlocking(Dispatchers.Default) {

        requestStatus = RequestStatus.REQUESTING

        val call = RetrofitClient.exchangeInterface.getLatest(baseCurrency)

        call.enqueue(object : Callback<Rates> {

            override fun onFailure(call: Call<Rates>, t: Throwable) {

                requestStatus = RequestStatus.FAILURE
            }

            override fun onResponse(
                call: Call<Rates>,
                response: Response<Rates>
            ) {

                val ratesResponse = response.body()

                if (ratesResponse != null) {

                    requestStatus = RequestStatus.SUCCESS

                    saveRates(ratesResponse)
                } else {

                    requestStatus = RequestStatus.FAILURE
                }

                // Todo: remember to put something that checks the base while ui rendering, validation
            }
        })
    }

    /* Just simply saves the Rates in db */
    private fun saveRates(rates: Rates) = runBlocking(Dispatchers.IO) {

        val dao = db.ratesDao()

        rates.apply {
            dateCreated = Date(System.currentTimeMillis())
        }

        dao.insertRate(rates)
    }

    fun stopUpdates() {

        shouldReceiveUpdates = false
    }

    /***
     * This function triggers the request repeater. It's an
     * active thread therefore here goes the GlobalScope.
     */
    fun startUpdates() = GlobalScope.launch {

        shouldReceiveUpdates = true

        repeat(60) {

            /* Two usages, forcefully stopping or waiting if the previous
                request is still in business */
            if (shouldReceiveUpdates && requestStatus != RequestStatus.REQUESTING) {

                getExchangesRates()

                delay(1000)
            }
        }
    }
}