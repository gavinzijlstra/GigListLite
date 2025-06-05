package com.gavinzijlstra.giglistlite

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers

interface GigService {
    @GET("gigs.php")
    //@Headers("X-API-KEY: 8b1cfa0b-4a61-4536-b92a-210463027220")
    @Headers("X-API-KEY: 12345")
    fun getGigs(): Call<List<Gig>>
}