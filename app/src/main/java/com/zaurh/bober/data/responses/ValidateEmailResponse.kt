package com.zaurh.bober.data.responses

import kotlinx.serialization.Serializable

@Serializable
data class ValidateEmailResponse(
    val success: Boolean,
    val message: String = "No message.",
    val email: String = ""
)
