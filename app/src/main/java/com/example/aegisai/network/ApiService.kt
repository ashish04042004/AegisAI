package com.example.aegisai.network

import com.example.aegisai.model.BriefingRequest
import com.example.aegisai.model.BriefingResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("generate_briefing")
    fun generateBriefing(@Body request: BriefingRequest): Call<BriefingResponse>
}
