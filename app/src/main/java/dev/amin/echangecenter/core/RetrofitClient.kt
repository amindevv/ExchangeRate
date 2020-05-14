package dev.amin.echangecenter.core

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import dev.amin.echangecenter.data.models.Rates
import retrofit2.Converter
import java.lang.reflect.Type


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

    val exchangeInterface =
        retrofit
            .addConverterFactory(
                createGsonConverter(
                    Rates::class.java,
                    RateGsonConverter()
                )
            )
            .build()
            .create(RatesNetworkInterface::class.java)

    private fun createGsonConverter(type: Type, typeAdapter: Any): Converter.Factory {

        val gson =
            GsonBuilder().registerTypeAdapter(type, typeAdapter).create()

        return GsonConverterFactory.create(gson)
    }
}