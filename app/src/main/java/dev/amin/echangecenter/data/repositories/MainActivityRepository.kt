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

    // Response is feed to DB and from DB values are feed to ViewModel
    private val db = AppDb.invoke(context)

    /*  This is the boss around here, responsible
        for cancellations and continuations */
    private var shouldReceiveUpdates = false

    /* Request Status is used for handling if the repo should
        make the next request or not */
    private var requestStatus = RequestStatus.NONE

    /* Reference to the database, VM uses this. ViewModel has no
        idea about where the data comes from, he just takes it from db*/
    val rates = db.ratesDao().getLiveRates()

    // BaseCurrency which is queried in to request
    var baseCurrency = "EUR"

    /* This is the job which is repeated every one second
        and calls the network request */
    private var exchangeRatesJob: Job? = null

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

                    Log.e("Rates Received", ratesResponse.baseCurrency)
                } else {

                    requestStatus = RequestStatus.FAILURE
                }
            }
        })
    }

    /* Just simply saves the Rates in db */
    private fun saveRates(rates: Rates) = runBlocking(Dispatchers.IO) {

        db.ratesDao().insertRate(rates)
    }

    fun startUpdates() {

        // Already running
        if (shouldReceiveUpdates)
            return

        shouldReceiveUpdates = true

        exchangeRatesJob = GlobalScope.launch {

            repeat(1000) {

                if (!shouldReceiveUpdates)
                    this.cancel()

                /* Two usages, forcefully stopping or waiting if the previous
                request is still in business */
                if (shouldReceiveUpdates && requestStatus != RequestStatus.REQUESTING) {

                    getExchangesRates()

                    delay(1000)
                }
            }
        }
    }

    fun stopUpdates() {

        shouldReceiveUpdates = false

        exchangeRatesJob?.cancel()
    }
}