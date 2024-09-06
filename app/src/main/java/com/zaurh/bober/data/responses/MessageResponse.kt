package com.zaurh.bober.data.responses

data class MessageResponse(
    val success: Boolean,
    val message: String = "Error message.",
)
