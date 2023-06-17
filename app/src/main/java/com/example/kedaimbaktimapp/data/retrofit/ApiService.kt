package com.example.kedaimbaktimapp.data.retrofit

import com.example.kedaimbaktimapp.data.response.transactionResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("v2/{id}/status")
    fun getTransaction(
        @Header("Authorization") header: String,
        @Path("id") id: String
    ): Call<transactionResponse>
}