package com.example.freezerwatchdog

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiAdapter {
    val apiClient: ApiClient = Retrofit.Builder()
        .baseUrl("http://192.168.1.9:8000/")
        .client(OkHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiClient::class.java)
}