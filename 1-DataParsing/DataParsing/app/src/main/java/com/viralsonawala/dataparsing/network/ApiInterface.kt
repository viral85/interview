package com.viralsonawala.dataparsing.network

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiInterface {

    @GET("{temperature}/date/{region}.txt")
    fun getTempratureData(@Path("temperature") temperature: String, @Path("region") region: String): Call<ResponseBody>
}