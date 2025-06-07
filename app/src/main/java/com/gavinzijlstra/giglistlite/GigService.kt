package com.gavinzijlstra.giglistlite

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface GigService {
    @GET("gigs.php")
    fun getGigs(
        @Header("X-API-KEY") authToken: String
    ): Call<List<Gig>>

    @POST("gigs.php")
    fun addGig(
        @Header("X-API-KEY") authToken: String,
        @Body gig: Gig
    ): Call<Void>

    @PUT("gigs.php")
    fun updateGig(
        @Header("X-API-KEY") authToken: String,
        @Body gig: Gig
    ): Call<Void>

    @DELETE("gigs.php")
    fun deleteGig(
        @Header("X-API-KEY") authToken: String,
        @Query("_id") id: String
    ): Call<Void>

}