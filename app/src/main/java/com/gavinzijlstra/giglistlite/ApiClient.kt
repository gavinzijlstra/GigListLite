package com.gavinzijlstra.giglistlite

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "http://giglistlite.tiquidesign.nl/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val gigService: GigService by lazy {
        retrofit.create(GigService::class.java)
    }
}