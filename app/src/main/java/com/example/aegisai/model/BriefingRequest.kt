package com.example.aegisai.model

data class BriefingRequest(
    val gps: String,
    val timestamp: String,
    val severity: Int,
    val analysis: String
)
