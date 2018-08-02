package com.viralsonawala.dataparsing.network

import com.viralsonawala.dataparsing.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.io.File
import java.util.concurrent.TimeUnit

object ApiClient {
    val BASE_URL by lazy {
        "https://www.metoffice.gov.uk/pub/data/weather/uk/climate/datasets/"
    }
    private var retrofit: Retrofit? = null

    private val client: Retrofit?
        get() {
            if (retrofit == null) {
                val builder = Retrofit.Builder()
                        .baseUrl(BASE_URL)
                val client = OkHttpClient.Builder()
                client.connectTimeout(30, TimeUnit.SECONDS)
                client.readTimeout(60, TimeUnit.SECONDS)
                client.writeTimeout(5, TimeUnit.MINUTES)

                if (BuildConfig.DEBUG) {
                    val loggingInterceptor = HttpLoggingInterceptor()
                    loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                    client.addInterceptor(loggingInterceptor)
                }

                builder.client(client.build())

                retrofit = builder.build()
            }
            return retrofit
        }


    val service: ApiInterface
        get() = client!!.create(ApiInterface::class.java)

}
