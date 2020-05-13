package dev.amin.echangecenter.core

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "https://hiring.revolut.codes/api/android/"

    /* Cache null is easy way to disable cache for me, u can also
        use the header */
    private val client = OkHttpClient.Builder()
        .cache(null)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val exchangeInterface =
        retrofit.create(RatesNetworkInterface::class.java)
}