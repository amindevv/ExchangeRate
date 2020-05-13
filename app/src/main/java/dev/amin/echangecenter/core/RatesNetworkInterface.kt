package dev.amin.echangecenter.core

import dev.amin.echangecenter.data.models.RatesResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RatesNetworkInterface {

    @GET("latest")
    fun getLatest(@Query("base") base: String = "EUR"): Call<RatesResponse>
}