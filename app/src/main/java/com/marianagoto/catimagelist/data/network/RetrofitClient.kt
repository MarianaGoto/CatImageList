package com.marianagoto.catimagelist.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.marianagoto.catimagelist.data.remote.api.CatApiService


object RetrofitClient {
    var retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://api.thecatapi.com/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    var service: CatApiService = retrofit.create(CatApiService::class.java)
}