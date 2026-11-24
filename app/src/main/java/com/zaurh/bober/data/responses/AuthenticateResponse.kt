package com.zaurh.bober.data.responses

import kotlinx.serialization.Serializable
@Serializable
data class AuthenticateResponse(
    val success: Boolean,
    val message: String = "No message.",
)
