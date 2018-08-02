package com.viralsonawala.articles.network

import com.viralsonawala.dataparsing.network.ApiInterface
import com.viralsonawala.mapgeojson.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private val BASE_URL by lazy {
        "https://www.example.com"
    }
    private var retrofit: Retrofit? = null

    private val client: Retrofit?
        get() {
            if (retrofit == null) {
                val builder = Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                val client = OkHttpClient.Builder()
                client.connectTimeout(30, TimeUnit.SECONDS)
                client.readTimeout(60, TimeUnit.SECONDS)
                client.writeTimeout(5, TimeUnit.MINUTES)

                // add logging interceptor in debug build
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
