package dev.amin.echangecenter.data.repositories

import android.util.Log
import dev.amin.echangecenter.core.RetrofitClient
import dev.amin.echangecenter.data.RequestStatus
import dev.amin.echangecenter.data.models.RatesResponse
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivityRepository {

    var baseCurrency = "EUR"

    private var shouldReceiveUpdates = false

    private var requestStatus = RequestStatus.NONE

    private fun getExchangesRates() = runBlocking(Dispatchers.Default) {

        requestStatus = RequestStatus.REQUESTING

        val call = RetrofitClient.exchangeInterface.getLatest(baseCurrency)

        call.enqueue(object : Callback<RatesResponse> {

            override fun onFailure(call: Call<RatesResponse>, t: Throwable) {

                requestStatus = RequestStatus.FAILURE
            }

            override fun onResponse(
                call: Call<RatesResponse>,
                response: Response<RatesResponse>
            ) {

                if (response.body() != null) {

                    requestStatus = RequestStatus.SUCCESS
                } else {

                    requestStatus = RequestStatus.FAILURE
                }

                // Todo: Here save the response!

                // Todo: remember to put something that checks the base while ui rendering, validation
            }
        })
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

            if (shouldReceiveUpdates && requestStatus != RequestStatus.REQUESTING) {

                Log.e("Rates Coroutine", it.toString() + " RequestStatus ${requestStatus.name}")

                getExchangesRates()

                delay(1000)
            }
        }
    }
}