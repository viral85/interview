package com.viralsonawala.dataparsing.network

import com.viralsonawala.articles.model.ArticlesResp
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface ApiInterface {

    @GET
    fun getArticles(@Url url: String): Call<ArticlesResp>
}