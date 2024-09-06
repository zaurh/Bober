package com.zaurh.bober.data.responses

data class AuthenticateResponse(
    val success: Boolean,
    val message: String = "No message.",
)
