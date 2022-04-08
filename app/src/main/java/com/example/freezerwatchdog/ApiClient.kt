package com.example.freezerwatchdog

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiClient {
    @GET("/systems/")
    suspend fun getFreezerSystemStatus(
        @Query("system_id") system_id: String
    ): Response<List<SystemStatusModel>>
}