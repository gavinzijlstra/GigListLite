package com.gavinzijlstra.giglistlite

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers

interface GigService {
    @GET("gigs.php")
    @Headers("X-API-KEY: 12345")
    fun getGigs(): Call<List<Gig>>
}